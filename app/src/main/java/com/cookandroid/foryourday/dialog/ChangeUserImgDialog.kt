package com.cookandroid.foryourday.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.cookandroid.foryourday.R
import com.cookandroid.foryourday.main.MainActivity
import com.cookandroid.foryourday.retrofit.*
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.ui.setting.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.net.URL

class ChangeUserImgDialog(private val context: Context, private val fragment: SettingFragment){
    private val dlg = Dialog(context)
    lateinit var userImg: ImageView

    fun show(){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_change_user_img)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        userImg = dlg.findViewById(R.id.user_img)
        val selectUserImg = dlg.findViewById<TextView>(R.id.select_user_img)
        val selectDefaultImg = dlg.findViewById<TextView>(R.id.select_default_img)
        val btnUserImgChangeCancel = dlg.findViewById<AppCompatButton>(R.id.btn_user_img_change_cancel)
        val btnUserImgChange = dlg.findViewById<AppCompatButton>(R.id.btn_user_img_change)

        CoroutineScope(Dispatchers.IO).launch {
            val sqlite = SQLite(context)
            val userData = sqlite.getUserInfo().await()
            var bm: Bitmap? = null
            if(userData.user.imagePath == null){
                userImg.setImageResource(R.drawable.ic_baseline_person_24)
            }else{
                kotlin.runCatching {
                    val url = URL(userData.user.imagePath)
                    val conn = url.openConnection()
                    conn.connect()
                    val bis = BufferedInputStream(conn.getInputStream())
                    bm = BitmapFactory.decodeStream(bis)
                    bis.close()
                }.onSuccess {
                    withContext(Dispatchers.Main){
                        userImg.setImageBitmap(bm)
                    }
                }.onFailure {
                    userImg.setImageResource(R.drawable.ic_baseline_person_24)
                }
            }
        }

        btnUserImgChangeCancel.setOnClickListener{
            dlg.dismiss()
        }

        selectDefaultImg.setOnClickListener {
            userImg.setImageResource(R.drawable.ic_baseline_person_24)
            userImg.tag = null
        }

        selectUserImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                type = "image/*"
            }
            fragment.startActivityForResult(intent, 1)
        }

        btnUserImgChange.setOnClickListener {
            if(userImg.tag == null){
                CoroutineScope(Dispatchers.Default).launch {
                    patchUserImage(null)
                }
            }else{
                val file = File(userImg.tag.toString())
                val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)

                ImgApiInterface.create().postImage(multipartBody).enqueue(
                    object : retrofit2.Callback<ImageData>{
                        override fun onResponse(call: Call<ImageData>, response: Response<ImageData>) {
                            if (response.isSuccessful){
                                if(response.code() == 201){
                                    CoroutineScope(Dispatchers.Default).launch {
                                        patchUserImage(response.body()!!.imagePath)
                                    }
                                }else{
                                    val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                                    Log.d("postImage", jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"))
                                }
                            }
                        }

                        override fun onFailure(call: Call<ImageData>, t: Throwable) {
                            Log.d("postImage", "error: $t")
                        }
                    }
                )
            }
        }

        dlg.show()
    }

    private suspend fun patchUserImage(path: String?){
        val sqlite = SQLite(context)
        val userInfo = sqlite.getUserInfo().await()
        val newData = UserInfo(null, null, userInfo.user.userName, path)
        val header = "bearerToken ${userInfo.oauth.accessToken}"
        ApiInterface.create().patchUser(header, userInfo.user.userId!!, newData).enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Main).launch {
                            if(path == null){
                                sqlite.patchUserProfileImage("")
                                setBitmap(null)
                            }else{
                                sqlite.patchUserProfileImage("https://www.image.todo.youlhyuk.com/$path")
                                setBitmap(userImg.tag.toString())
                            }
                            Toast.makeText(context, "${userInfo.user.userName}님! 변경이 완료되었습니다!👌", Toast.LENGTH_SHORT).show()
                            dlg.dismiss()
                        }
                    }else if(response.code() in 400..500){
                        val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                        Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (t is IOException) {
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("patchUserName", "error: $t")
                    }
                }
            }
        )
    }

    private fun setBitmap(path: String?){
        if(path == null){
            (context as MainActivity).header.findViewById<ImageView>(R.id.user_profile_img).setImageResource(R.drawable.ic_baseline_person_24)
            fragment.profileImg.setImageResource(R.drawable.ic_baseline_person_24)
        }else{
            val bm = BitmapFactory.decodeFile(path)
            (context as MainActivity).header.findViewById<ImageView>(R.id.user_profile_img).setImageBitmap(bm)
            fragment.profileImg.setImageBitmap(bm)
        }
    }
}