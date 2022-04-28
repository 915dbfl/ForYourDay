package com.cookandroid.foryourday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import org.json.JSONObject


class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var context: Context

    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private val clientName = "ForYourDay"

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var userInfo = UserInfo(null, null, null)
    private var oauthInfo = OAuthInfo(null, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            }

            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object: NidProfileCallback<NidProfileResponse>{
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.d("callProfileApi", "errorCode: ${httpStatus.toString()}")
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
            Log.d("handleSignInResult", "errorCode: ${e.statusCode}")
        }
    }

    private fun checkUser(){
        ApiInterface.create().checkUser(userInfo).enqueue(
            object : retrofit2.Callback<UserData>{
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if(response.isSuccessful){
                        userInfo = response.body()!!.user
                        oauthInfo = response.body()!!.oauth
                        val data = UserData(oauthInfo, userInfo)
                        gotoMain(data)
                    }else{
                        if(response.code() in 400..500){
                            val jObjError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Log.d("postApi", "error: ${t.toString()}")
                }
            }
        )
    }

    private fun gotoMain(userData: UserData){
        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.putExtra("data", userData)
        startActivity(mainIntent)
        Toast.makeText(context, "${userData.user.userName}님! 반갑습니다!", Toast.LENGTH_SHORT).show()
    }
}