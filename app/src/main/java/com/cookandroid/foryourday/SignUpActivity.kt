package com.cookandroid.foryourday

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.foryourday.databinding.ActivitySignUpBinding
import com.cookandroid.foryourday.login.LoginActivity
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.UserData
import com.cookandroid.foryourday.retrofit.UserInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var context: Context
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private val clientName = "ForYourDay"
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val userInfo = UserInfo(null, null, null, null)

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


        binding.btnGoogleLogin.setOnClickListener {
            val googleSignInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(googleSignInIntent, 100)
        }

        binding.btnCheckId.setOnClickListener {
            if(binding.edtUserName.text.toString().replace(" ", "") != ""){
                userInfo.userName = binding.edtUserName.text.toString()
                binding.checkUser.isChecked = true
                binding.layoutGetName.visibility = View.GONE
            }else{
                Toast.makeText(context, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnHome.setOnClickListener {
            val loginIntent = Intent(context, LoginActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
        }

        binding.btnSignUp.setOnClickListener {
            postApi()
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
                        Toast.makeText(context, "${userInfo.userName}님! 회원가입에 성공하셨습니다!\n로그인을 진행해주세요!☺", Toast.LENGTH_SHORT).show()
                        val loginIntent = Intent(context, LoginActivity::class.java)
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(loginIntent)
                    }else{
                        if(response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show()
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}