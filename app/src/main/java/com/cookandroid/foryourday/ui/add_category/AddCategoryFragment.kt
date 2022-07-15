package com.cookandroid.foryourday.ui.add_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.foryourday.databinding.FragmentAddCategoryBinding
import com.cookandroid.foryourday.dialog.ColorPickerDialog
import com.cookandroid.foryourday.dialog.DeleteCategoryDialog
import com.cookandroid.foryourday.retrofit.ApiInterface
import com.cookandroid.foryourday.retrofit.Category
import com.cookandroid.foryourday.retrofit.CategoryData
import com.cookandroid.foryourday.sqlite.SQLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class AddCategoryFragment : androidx.fragment.app.Fragment(){
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    private var layoutChosenColor: LinearLayout? = null
    private var layoutCreateCategory: LinearLayout? = null
    private var layoutCategoryBtns: LinearLayout? = null
    private var colorString: String = "#e0e0e0"
    var addCategoryRecyclerViewAdapter: AddCategoryRecyclerViewAdapter? = null
    var btnDeleteComplete: AppCompatButton? = null
    var btnDeleteCancel: AppCompatButton? = null
    var addCategoryMessage: TextView? = null
    var btnDeleteCategory: AppCompatButton? = null
    var btnAddCategory: AppCompatButton? = null
    var edtCategoryName: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutChosenColor = binding.layoutChosenColor
        layoutCreateCategory = binding.layoutCreateCategory
        layoutCategoryBtns = binding.layoutCategoryBtns
        btnDeleteComplete = binding.btnDeleteComplete
        btnDeleteCancel = binding.btnDeleteCancel
        btnDeleteCategory = binding.btnDeleteCategory
        btnAddCategory = binding.btnAddCategory
        addCategoryMessage = binding.addCategoryMessage
        edtCategoryName = binding.edtCategoryName
        val categoryRecyclerview = binding.categoryRecyclerview
        val btnColorPicker = binding.btnColorPicker
        val btnCancelCreate = binding.btnCancelCreate
        val btnCreateCategory = binding.btnCreateCategory
        val sqlite = SQLite(context!!)

        CoroutineScope(Dispatchers.Main).launch {
            val categories = sqlite.getCategories().await()

            addCategoryRecyclerViewAdapter = AddCategoryRecyclerViewAdapter(categories, false)
            categoryRecyclerview.layoutManager = LinearLayoutManager(context)
            categoryRecyclerview.adapter = addCategoryRecyclerViewAdapter

            if(categories.isEmpty()){
                addCategoryMessage!!.visibility = View.VISIBLE
                btnDeleteCategory!!.visibility = View.GONE
            }
        }

        btnDeleteCategory!!.setOnClickListener {
            btnAddCategory!!.visibility = View.GONE
            btnDeleteCategory!!.visibility = View.GONE
            btnDeleteComplete!!.visibility = View.VISIBLE
            btnDeleteCancel!!.visibility = View.VISIBLE
            addCategoryRecyclerViewAdapter!!.setValue(null, true)
        }

        btnDeleteCancel!!.setOnClickListener {
            btnAddCategory!!.visibility = View.VISIBLE
            btnDeleteCategory!!.visibility = View.VISIBLE
            btnDeleteComplete!!.visibility = View.GONE
            btnDeleteCancel!!.visibility = View.GONE
            addCategoryRecyclerViewAdapter!!.setValue(null, false)
        }

        btnDeleteComplete!!.setOnClickListener {
            val list = addCategoryRecyclerViewAdapter!!.selectedList
            if(list.size == 0){
                Toast.makeText(context, "삭제할 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()
            }else{
                val dlg = DeleteCategoryDialog(context!!)
                dlg.show(list, this)
            }
        }

        btnAddCategory!!.setOnClickListener {
            layoutCreateCategory!!.visibility = View.VISIBLE
            layoutCategoryBtns!!.visibility = View.GONE
        }

        btnCancelCreate.setOnClickListener {
            layoutCategoryBtns!!.visibility = View.VISIBLE
            layoutCreateCategory!!.visibility = View.GONE
        }

        btnColorPicker.setOnClickListener {
            openColorPicker()
        }

        btnCreateCategory.setOnClickListener {
            if(edtCategoryName!!.text.toString().replace(" ", "") != ""){
                val colorData = CategoryData(edtCategoryName!!.text.toString(), colorString, null)
                CoroutineScope(Dispatchers.Main).launch {
                    postApi(colorData)
                }
            }else{
                Toast.makeText(context, "카테고리 이름을 설정해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openColorPicker(){
        ColorPickerDialog
            .Builder(context!!)
            .setColorListener { color, colorHex ->
                layoutChosenColor!!.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
                colorString = colorHex
            }.show()
    }

    private suspend fun postApi(categoryData: CategoryData){
        val sqlite = SQLite(context!!)
        val header = "bearerToken ${sqlite.getUserInfo().await().oauth.accessToken}"
        ApiInterface.create().addCategory(header, categoryData).enqueue(
            object : retrofit2.Callback<Category>{
                override fun onResponse(call: Call<Category>, response: Response<Category>) {
                    if(response.isSuccessful){
                        Log.d("context 확인", context.toString())
                        Toast.makeText(context, "카테고리가 생성되었습니다!☺", Toast.LENGTH_SHORT).show()
                        edtCategoryName!!.text.clear()
                        layoutCreateCategory!!.visibility = View.GONE
                        layoutCategoryBtns!!.visibility = View.VISIBLE
                        CoroutineScope(Dispatchers.Main).launch {
                            categoryData.id = response.body()!!.category.id
                            sqlite.addCategoryDB(categoryData)
                            val categories = sqlite.getCategories().await()
                            addCategoryRecyclerViewAdapter!!.setValue(categories,false)
                            if(categories.isEmpty()){
                                addCategoryMessage!!.visibility = View.VISIBLE
                                btnAddCategory!!.visibility = View.VISIBLE
                                btnDeleteCategory!!.visibility = View.GONE
                            }else{
                                btnDeleteCategory!!.visibility = View.VISIBLE
                                btnAddCategory!!.visibility = View.VISIBLE
                                addCategoryMessage!!.visibility = View.GONE
                            }
                        }
                    }else{
                        if (response.code() in 400..500){
                            val jObjectError = JSONObject(response.errorBody()!!.charStream().readText())
                            Toast.makeText(context, jObjectError.getJSONArray("errors").getJSONObject(0).getString("message"),Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<Category>, t: Throwable) {
                    if(t is IOException){
                        Toast.makeText(context, "네트워크를 확인해주세요!🙄", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("categoryPostApi", "error: $t")
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}