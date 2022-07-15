package com.cookandroid.foryourday.ui.setting

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cookandroid.foryourday.BuildConfig
import com.cookandroid.foryourday.login.LoginActivity
import com.cookandroid.foryourday.databinding.FragmentSettingBinding
import com.cookandroid.foryourday.dialog.ChangeUserImgDialog
import com.cookandroid.foryourday.dialog.ChangeUserNameDialog
import com.cookandroid.foryourday.dialog.DeleteUserDialog
import com.cookandroid.foryourday.sqlite.SQLite
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.cookandroid.foryourday.main.MainActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cookandroid.foryourday.R
import kotlinx.coroutines.withContext
import java.io.*

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    lateinit var profileImg: ImageView
    lateinit var profileUserName: TextView
    private lateinit var profileUserEmail: TextView
    private lateinit var imgDlg: ChangeUserImgDialog

    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        profileUserEmail = binding.profileUserEmail
        profileUserName = binding.profileUserName
        profileImg = binding.profileImg


        val clientId = BuildConfig.CLINET_ID
        val clientSecret = BuildConfig.CLIENT_SECRET
        val clientName = "ForYourDay"

        NaverIdLoginSDK.apply{
            showDevelopersLog(true)
            initialize(context!!, clientId, clientSecret, clientName)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sqlite = SQLite(context!!)
        CoroutineScope(Dispatchers.Default).launch {
            val userData = sqlite.getUserInfo().await()
            if (userData.user.imagePath == ""){
                profileImg.setImageResource(R.drawable.ic_baseline_person_24)
            }else{
                withContext(Dispatchers.Main){
                    Glide.with(this@SettingFragment).load(userData.user.imagePath).error(R.drawable.ic_baseline_person_24).into(profileImg)
                }
            }
            withContext(Dispatchers.Main){
                profileUserEmail.text = userData.user.email
                profileUserName.text = userData.user.userName
            }
        }

        binding.changeProfileImg.setOnClickListener {
            imgDlg = ChangeUserImgDialog(context!!, this)
            imgDlg.show()
        }

        binding.changeProfileName.setOnClickListener {
            val dlg = ChangeUserNameDialog(context!!)
            dlg.show(this)
        }

        binding.userLogOut.setOnClickListener {
            val lst =  profileUserEmail.text.split("@")
            val intent = Intent(context!!, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            val sqlite1 = SQLite(context!!)
            if(lst[1] == "naver.com"){
                Toast.makeText(context!!, "로그아웃되었습니다!", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Default).launch {
                    NaverIdLoginSDK.logout()
                    sqlite1.deleteUser()
                    startActivity(intent)
                }
            }else{
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener((context as MainActivity)) {
                        Toast.makeText(context!!, "로그아웃되었습니다!", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Default).launch {
                            sqlite1.deleteUser()
                            startActivity(intent)
                        }
                    }
            }
        }

        binding.userDeleteAccount.setOnClickListener {
            val dlg = DeleteUserDialog(context!!, profileUserEmail.text.toString())
            dlg.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 1){
            val uri = data!!.data
            if(uri != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    val source = ImageDecoder.createSource(context!!.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    imgDlg.userImg.setImageBitmap(bitmap)
                }else{
                    val bm = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
                    imgDlg.userImg.setImageBitmap(bm)
                }
                imgDlg.userImg.tag = getRealPath(uri)
            }
        }
    }

    private fun getRealPath(uri: Uri): String?{
        val contentResolver = context!!.contentResolver
        val filePath = context!!.applicationInfo.dataDir + File.separator + System.currentTimeMillis() + ".jpg"
        val file = File(filePath)

        try{
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while(inputStream.read(buf).also { len = it } > 0)
                outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        }catch(ignore: IOException){
            return null
        }
        Log.d("절대경로 확인", file.absolutePath.toString())
        return file.absolutePath
    }

}