package com.cookandroid.foryourday.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.foryourday.BuildConfig
import com.cookandroid.foryourday.SignUpActivity
import com.cookandroid.foryourday.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Response
import com.cookandroid.foryourday.main.MainActivity
import com.cookandroid.foryourday.retrofit.*
import com.cookandroid.foryourday.sqlite.SQLite
import com.cookandroid.foryourday.sqlite.UserInfoDBHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException


class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var context: Context

    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private val clientName = "ForYourDay"

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var userInfo = UserInfo(null, null, null,  null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        context = this

        setContentView(binding.root)
        val userName = checkLogin()
        if(userName != null){
            val mainIntent = Intent(context, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainIntent)
            Toast.makeText(context, "${userName}님! 반갑습니다! 🤗", Toast.LENGTH_SHORT).show()
        }else{
            init()
        }
    }

    private fun init(){
        clientId = BuildConfig.CLINET_ID
        clientSecret = BuildConfig.CLIENT_SECRET

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

        NaverIdLoginSDK.apply{
            showDevelopersLog(true)
            initialize(context, clientId, clientSecret, clientName)
        }

        binding.buttonOAuthLoginImg.setOAuthLoginCallback(object: OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                Log.d("OAuthLoginCallback", "errorCode: $errorCode")
                Toast.makeText(context, "OAuthLoginCallback: $errorCode", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object: NidProfileCallback<NidProfileResponse>{
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        Toast.makeText(context, "로그인 도중 오류가 발생했습니다.\n다시 시도해주세요!😔",Toast.LENGTH_SHORT).show()
                        Log.e("naverIdLogin", "errorCode:$errorCode, errorDesc:$errorDescription")
                    }

                    override fun onSuccess(result: NidProfileResponse) {
                        userInfo.email = result.profile!!.email
                        checkUser()
                    }
                })
            }
        })


        binding.buttonGoogleOAuth.setOnClickListener {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if(account != null) {
                userInfo.email = account.email
                checkUser()
            }else{
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, 100)
            }
        }

        binding.textSignUp.setOnClickListener {
            val signUpIntent = Intent(context, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account = completedTask.getResult(ApiException::class.java)
            userInfo.email = account.email
            checkUser()
        }catch (e: ApiException){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUser(){
        val sqlite = SQLite(context)
        ApiInterface.create().checkUser(userInfo).enqueue(
            object : retrofit2.Callback<UserData>{
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if(response.isSuccessful){
                        CoroutineScope(Dispatchers.Default).launch {
                            makeUserDB(response)
                            sqlite.makeCategoriesDB(response.body()!!.oauth.accessToken!!)
                            sqlite.makeTodoDB(response.body()!!.oauth.accessToken!!)
                            sqlite.makeDDayDB(response.body()!!)
                        }
                    }else{
                        if(response.code() in 400..500){
                            val jObjError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("postApi", "error: $t")
                    }
                }
            }
        )
    }

    private fun makeUserDB(res: Response<UserData>){
        val helper = UserInfoDBHelper(context)
        val sql = """
            insert into UserTable (userId, email, userName, imagePath, accessToken, refreshToken) 
            values(?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val arg = arrayOf(res.body()!!.user.userId, res.body()!!.user.email, res.body()!!.user.userName, res.body()!!.user.imagePath, res.body()!!.oauth.accessToken, res.body()!!.oauth.refreshToken)

        helper.writableDatabase.execSQL(sql, arg)
        helper.writableDatabase.close()
    }

    private fun checkLogin(): String?{
        val helper = UserInfoDBHelper(context)
        val sql = "select * from UserTable"
        val cur = helper.writableDatabase.rawQuery(sql, null)
        return if (cur.count > 0) {
            cur.moveToFirst()
            val idx1 = cur.getColumnIndex("userName")
            val result = cur.getString(idx1)
            cur.close()
            result
        } else {
            cur.close()
            null
        }
    }
}