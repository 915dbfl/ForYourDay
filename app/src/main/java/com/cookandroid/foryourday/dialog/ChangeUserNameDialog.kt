package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.main.MainActivity
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.UserInfo
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.ui.setting.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class ChangeUserNameDialog(private val context: Context)  {
    private val dlg = Dialog(context)

    fun show(fragment: SettingFragment){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_change_user_name)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnUserNameModifyCancel = dlg.findViewById<AppCompatButton>(R.id.btn_user_name_modify_cancel)
        val btnUserNameModify = dlg.findViewById<AppCompatButton>(R.id.btn_user_name_modify)
        val textViewUserName = dlg.findViewById<EditText>(R.id.textview_user_name)
        CoroutineScope(Dispatchers.Main).launch {
            val sqlite = SQLite(context)
            val userData = sqlite.getUserInfo().await()
            textViewUserName.setText(userData.user.userName)
        }

        btnUserNameModifyCancel.setOnClickListener {
            dlg.dismiss()
        }

        btnUserNameModify.setOnClickListener {
            val name = textViewUserName.text.toString()
            if(name.replace(" ", "") == ""){
                Toast.makeText(context, "사용자 이름을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }else{
                CoroutineScope(Dispatchers.Default).launch{
                    patchUserName(name, fragment)
                }
                dlg.dismiss()
            }
        }

        dlg.show()
    }

    private suspend fun patchUserName(name: String, fragment: SettingFragment){
        val sqlite = SQLite(context)
        val userInfo = sqlite.getUserInfo().await()
        val newData = UserInfo(null, null, name, userInfo.user.imagePath)
        val header = "bearerToken ${userInfo.oauth.accessToken}"
        ApiInterface.create().patchUser(header, userInfo.user.userId!!, newData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            sqlite.patchUserName(name)
                            (context as MainActivity).header.findViewById<TextView>(R.id.user_id).text = name
                            fragment.profileUserName.text = name
                            Toast.makeText(context, "${name}님! 변경이 완료되었습니다!👌", Toast.LENGTH_SHORT).show()
                        }
                    }else if(response.code() in 400..500){
                        val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                        Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("patchUserName", "error: $t")
                }
            }
        )
    }
}