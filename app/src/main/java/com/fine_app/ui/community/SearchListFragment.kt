package com.fine_app.ui.community

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.MainActivity
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentSearchListBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class SearchListFragment : Fragment() {
    private var _binding: FragmentSearchListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var myID by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchListBinding.inflate(layoutInflater)
        val root: View = binding.root

        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(true)

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myID = userInfo.getString("userInfo", "2")!!.toLong()
        val text=arguments?.getString("text")!!
        if (text != "") {
            binding.searchView.setQuery(text, false)
            searchPosting(text!!)
        }

        val items = arrayOf("닉네임", "키워드")
        val spinner: Spinner = binding.spinner5
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> binding.keywordBox.visibility=View.INVISIBLE
                    1 -> binding.keywordBox.visibility=View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                searchPosting(p0)
                return true
            }
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        binding.cancelButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            //todo 백버튼
        }
        return root
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val title: TextView = itemView.findViewById(R.id.searchTitle)!!
        //private val content: TextView =itemView.findViewById(R.id.searchContent)!!
        private val groupCheck: TextView =itemView.findViewById(R.id.searchGroup)!!
        private val commentNum: TextView =itemView.findViewById(R.id.searchComment)!!
        private val writtenTime: TextView =itemView.findViewById(R.id.searchDate)!!
        private val commentImage: ImageView =itemView.findViewById(R.id.imageView13 )!!

        fun bind(post: Post) {
            this.post=post
            //content.text=this.post.content
            title.text=this.post.title
            val token=this.post.createdDate.split("-", "T", ":")
            writtenTime.text=token[1]+"/"+token[2]+" "+token[3]+":"+token[4]
            if(this.post.closingCheck) {
                commentImage.visibility=View.INVISIBLE
                commentNum.text="마감"
            }
            else {
                commentImage.visibility=View.VISIBLE
                commentNum.text=this.post.commentCount
            }

            if (this.post.groupCheck){ //그룹포스트
                groupCheck.text="그룹 커뮤니티"
            }else{
                groupCheck.text="일반 커뮤니티"
            }

            itemView.setOnClickListener{
                if (this.post.groupCheck){ //그룹포스트
                    viewGroupPosting(this.post.postingId, myID)

                }else{
                    viewMainPosting(this.post.postingId, myID)
                }
            }
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_search, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    //------------------------------API 연결------------------------------------

    private fun searchPosting(title:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.searchPosting(title = title) ?:return

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "커뮤니티 글 검색 - 응답 성공 / t : ${response.raw()}")
                val adapter=MyAdapter(response.body()!!)
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context)
                recyclerView.adapter=adapter
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "커뮤니티 글 검색 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewGroupPosting(postingId:Long?, memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewPosting(postingId = term, memberId = memberId) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : Callback<Post> {
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")

                val bundle= bundleOf("postingId" to postingId)
                findNavController().navigate(R.id.action_navigation_chatList_to_navigation_chattingRoom, bundle)
                //todo 네비 수정
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }

        })
    }
    private fun viewMainPosting(postingId:Long?, memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.viewPosting(postingId = term, memberId=memberId) ?:return

        //enqueue 하는 순간 네트워킹
        call.enqueue(object : Callback<Post> {
            //응답성공
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", response.body().toString())

                val bundle= bundleOf("postingId" to postingId)
                findNavController().navigate(R.id.action_navigation_chatList_to_navigation_chattingRoom, bundle)
                //todo 네비 수정
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "메인 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(false)
    }
}