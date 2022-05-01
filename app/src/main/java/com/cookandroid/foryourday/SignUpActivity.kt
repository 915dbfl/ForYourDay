package com.cookandroid.foryourday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.foryourday.databinding.ActivitySignUpBinding
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.UserData
import com.cookandroid.foryourday.retrofit.OAuthInfo
import com.cookandroid.foryourday.retrofit.UserInfo
import com.cookandroid.foryourday.sqlite.DBHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var context: Context

    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private val clientName = "ForYourDay"

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val userInfo = UserInfo(null, null, null)
    private val oauthInfo = OAuthInfo(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        context = this
        clientId = BuildConfig.CLINET_ID
        clientSecret = BuildConfig.CLIENT_SECRET

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(context, clientId, clientSecret, clientName)
        }

        binding.btnNaverLogin.setOAuthLoginCallback(object: OAuthLoginCallback{
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse>{
                    override fun onSuccess(result: NidProfileResponse) {
                        userInfo.email = result.profile!!.email
                        binding.checkSocial.isChecked = true
                        binding.layoutSocial.visibility = View.GONE
                        binding.layoutGetName.visibility = View.VISIBLE
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.d("callProfileApi", "fail to callProfileApi")
                    }

                    override fun onError(errorCode: Int, message: String) {
                        Log.d("callProfileApi", "errorCode: " + errorCode.toString())
                    }
                })
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                Log.d("OAuthLoginCallback", "errorCode: $errorCode")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })

        binding.btnGoogleLogin.setOnClickListener {
            val googleSignInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(googleSignInIntent, 100)
        }

        binding.btnCheckId.setOnClickListener {
            userInfo.userName = binding.edtUserName.text.toString()
            binding.checkUser.isChecked = true
            binding.layoutGetName.visibility = View.GONE
            binding.edtUserName.hideKeyBoard()
        }

        binding.btnHome.setOnClickListener {
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            postApi()
            finish()
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
            binding.checkSocial.isChecked = true
            binding.layoutSocial.visibility = View.GONE
            binding.layoutGetName.visibility = View.VISIBLE
        }catch (e: ApiException){
            Log.d("googleException", "failed code = " + e.statusCode)
        }
    }

    private fun postApi(){
        ApiInterface.create().addUser(userInfo).enqueue(
            object : retrofit2.Callback<UserData>{
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if(response.isSuccessful){
                        userInfo.userId = response.body()!!.user.userId
                        oauthInfo.accessToken = response.body()!!.oauth.accessToken
                        oauthInfo.refreshToken = response.body()!!.oauth.refreshToken
                        addUserInfo()
                        Toast.makeText(context, "${userInfo.userName}님! 회원가입에 성공하셨습니다!☺", Toast.LENGTH_SHORT).show()
                    }else{
                        if(response.code() in 400..500){
                            val jOBjError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jOBjError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Log.d("postApi", "error: ${t.toString()}")
                }
            }
        )
    }

    private fun addUserInfo(){
        val helper = DBHelper(context)

        val sql = """
            insert into UserTable (accessToken, refreshToken, userId, userName, email)
            values(?, ?, ?, ?, ?)
        """.trimIndent()

        val arg = arrayOf(oauthInfo.accessToken, oauthInfo.refreshToken, userInfo.userId, userInfo.userName, userInfo.email)

        helper.writableDatabase.execSQL(sql, arg)
        helper.writableDatabase.close()
    }

    private fun View.hideKeyBoard(){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}