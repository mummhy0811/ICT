package com.fine_app.ui.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.EditPost
import com.fine_app.MainActivity
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentPostingBinding
import com.fine_app.databinding.FragmentPostingEditBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response


class PostingEditFragment : Fragment() {
    private var _binding: FragmentPostingEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostingEditBinding.inflate(layoutInflater)
        val root: View = binding.root
        lateinit var text:String
        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(true)
        val postTitle=arguments?.getString("title")!!
        val postContent=arguments?.getString("content")!!
        val postingId=arguments?.getLong("postingId")!!


        binding.viewTitle.text=postTitle // 타이틀은 수정 불가
        binding.inputContents.setText(postContent) //기존 내용으로 텍스트 설정

        binding.inputContents.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=binding.inputContents.text.toString()
            }
        })
        binding.backButton.setOnClickListener{ //뒤로가기
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            //todo 백버튼
        }
        binding.finButton.setOnClickListener{ //등록
            editPosting(postingId, EditPost(text = text))
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            //todo 백버튼
        }
        return root
    }
    private fun editPosting(postingId:Long, text: EditPost){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.editPosting(postingId = postingId, text= text) ?:return
        call.enqueue(object : retrofit2.Callback<Post>{
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글 수정 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "글 수정 : ${response.body().toString()}")
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글 수정 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(false)
    }
}