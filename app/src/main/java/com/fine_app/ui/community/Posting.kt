package com.fine_app.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.*
import com.fine_app.Posting
import com.fine_app.databinding.CommunityMainPostBinding
import com.fine_app.databinding.CommunityPostingBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class Posting : AppCompatActivity(), CustomDialog.CustomDialogInterface {
    //private lateinit var binding: CommunityPostingBinding
    private lateinit var binding: CommunityMainPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //binding = CommunityPostingBinding.inflate(layoutInflater)
        binding = CommunityMainPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*
        lateinit var title:String
        lateinit var content:String
        val memberID=123123123123 //todo 멤버아이디 불러오기
        var capacity=2
        val spinner: Spinner = binding.spinner
        val items = arrayOf("인원 선택", 2, 3, 4, 5, 6)
        var groupcheck=false
        val groupCheckList: RadioGroup =binding.groupCheck
        groupCheckList.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId){
                R.id.radioButton_normal -> {
                    groupcheck=false
                    spinner.visibility=View.INVISIBLE
                }
                R.id.radioButton_group -> {
                    groupcheck=false
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
            val customDialog = CustomDialog("글쓰기를 취소하시겠습니까?", this, this)
            customDialog.show(supportFragmentManager, "CustomDialog")
            //finish()
        }
        binding.finButton.setOnClickListener{ //등록
            if(groupcheck){
                val newPost= GroupPosting(title, content, groupcheck, capacity)
                addGroupPost(memberID, newPost)

            }else{
                val newPost=Posting(title, content, groupcheck)
                addMainPost(memberID, newPost)
            }
            finish()
        }

 */

    }

    override fun onYesButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onCancelButtonClicked() {
        Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show()
    }



    private fun addMainPost(memberId:Long?, postInfo:Posting){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= memberId ?:0
        val call = iRetrofit?.addMainPost(memberId = term, postInfo) ?:return

        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "id:  ${memberId}")
                Log.d("retrofit", "title:  ${postInfo.title}")
                Log.d("retrofit", "content:  ${postInfo.content}")
                Log.d("retrofit", "groupCheck:  ${postInfo.groupCheck}")
            }
            //응답실패
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
            //응답성공
            override fun onResponse(call: Call<GroupPosting>, response: Response<GroupPosting>) {
                Log.d("retrofit", "글쓰기 - 응답 성공 / t : ${response.raw()}")
            }
            //응답실패
            override fun onFailure(call: Call<GroupPosting>, t: Throwable) {
                Log.d("retrofit", "글쓰기 - 응답 실패 / t: $t")
            }
        })
    }
}