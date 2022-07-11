package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.login.LoginActivity
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.sqlite.SQLite
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class DeleteUserDialog(private val context: Context, private val email: String){
    private val dlg = Dialog(context)

    fun show(){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_delete_user)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnDelete = dlg.findViewById<AppCompatButton>(R.id.btn_delete)
        val btnCancel = dlg.findViewById<AppCompatButton>(R.id.btn_cancel)

        btnDelete.setOnClickListener {
            val lst = email.split("@")
            if(lst[1] == "naver.com"){
                NidOAuthLogin().callDeleteTokenApi(context, object : OAuthLoginCallback {
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.d("callDeleteTokenApi", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                        Log.d("callDeleteTokenApi", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
                    }

                    override fun onSuccess() {
                        CoroutineScope(Dispatchers.Default).launch {
                            dlg.dismiss()
                            deleteUserApi()
                        }

                    }
                })
            }else{
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
                mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener{
                        CoroutineScope(Dispatchers.Default).launch {
                            dlg.dismiss()
                            deleteUserApi()
                        }
                    }
            }

        }

        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    private suspend fun deleteUserApi(){
        val sqlite = SQLite(context)
        val userInfo = sqlite.getUserInfo().await()
        val header = "bearerToken ${userInfo.oauth.accessToken}"
        val intent = Intent(context, LoginActivity::class.java)
        ApiInterface.create().deleteUser(header, userInfo.user.userId!!).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        sqlite.deleteUser()
                        Toast.makeText(context, "탈퇴되었습니다!\n그동안 감사했습니다.🤗", Toast.LENGTH_SHORT).show()
                        startActivity(context, intent, null)
                    }else{
                        if(response.code() in 400..500){
                            val jObjError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else {
                        Log.d("deleteUserApi", "error: $t")
                    }
                }
            }
        )
    }
}