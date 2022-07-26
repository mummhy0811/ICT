package com.fine_app.ui.community

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.fine_app.GroupPosting
import com.fine_app.Post
import com.fine_app.Posting
import com.fine_app.databinding.CommunityGroupPostBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class Posting : AppCompatActivity(), ConfirmDialogInterface {
    //private lateinit var binding: CommunityPostingBinding
    private lateinit var binding: CommunityGroupPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //binding = CommunityPostingBinding.inflate(layoutInflater)
        binding = CommunityGroupPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        lateinit var title:String
        lateinit var content:String
        val myID=123123123123 //todo 내 아이디 불러오기
        var capacity=2
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 2, 3, 4, 5, 6)
        var groupCheck=false
        val groupCheckList: RadioGroup =binding.groupCheck
        groupCheckList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_normal -> {
                    groupCheck=false
                    spinner.visibility=View.INVISIBLE
                }
                R.id.radioButton_group -> {
                    groupCheck=false
                    spinner.visibility= View.VISIBLE
                }
            }
        }
        spinner.adapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> capacity=2
                    1 -> capacity=2
                    2 -> capacity=3
                    3 -> capacity=4
                    4 -> capacity=5
                    5 -> capacity=6
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.inputTitle.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                title=binding.inputTitle.text.toString()
            }
        })
        binding.inputContents.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                content=binding.inputContents.text.toString()
            }
        })
        binding.backButton.setOnClickListener{ //뒤로가기
            val dialog = ConfirmDialog(this, "글쓰기를 취소하시겠습니까?", 0, 0)
            dialog.isCancelable = false
            dialog.show(this.supportFragmentManager, "ConfirmDialog")
        }
        binding.finButton.setOnClickListener{ //등록
            if(groupCheck){
                val newPost= GroupPosting(title, content, groupCheck, capacity)
                addGroupPost(myID, newPost)

            }else{
                val newPost=Posting(title, content, groupCheck)
                addMainPost(myID, newPost)
            }
            finish()
        }

         */

    }


    override fun onYesButtonClick(num: Int, theme:Int) {
        finish()
    }

    private fun addMainPost(memberId:Long?, postInfo:Posting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberId ?:0
        val call = iRetrofit?.addMainPost(memberId = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<Post>{

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글쓰기 - 응답 실패 / t: $t")
            }
        })
    }
    private fun addGroupPost(memberID:Long?, postInfo:GroupPosting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberID ?:0
        val call = iRetrofit?.addGroupPost(memberId = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<GroupPosting>{

            override fun onResponse(call: Call<GroupPosting>, response: Response<GroupPosting>) {
                Log.d("retrofit", "그룹 글쓰기 - 응답 성공 / t : ${response.raw()}")
            }

            override fun onFailure(call: Call<GroupPosting>, t: Throwable) {
                Log.d("retrofit", "그룹 글쓰기 - 응답 실패 / t: $t")
            }
        })
    }
}