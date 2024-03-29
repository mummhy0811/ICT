package com.fine_app.ui.friendList

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
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Friend
import com.fine_app.MainActivity
import com.fine_app.R
import com.fine_app.databinding.FragmentPostingBinding
import com.fine_app.databinding.FragmentSearchFriendListBinding
import com.fine_app.databinding.FriendSearchBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class SearchFriendListFragment : Fragment() {
    private var _binding: FragmentSearchFriendListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var myID by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    lateinit var keyword:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchFriendListBinding.inflate(layoutInflater)
        val root: View = binding.root

        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(false)

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myID = userInfo.getString("userInfo", "2")!!.toLong()



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
                searchFriend(p0)
                return true
            }
            override fun onQueryTextChange(p0: String): Boolean {
                return true
            }
        })
        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            //todo 백버튼
        }
        binding.keywordGroup.setOnCheckedChangeListener { _, id ->
            when(id){ //todo 키워드 검색 api보고 수정정
                R.id.keyword1 -> keyword="1"
                R.id.keyword2 -> keyword="1"
                R.id.keyword3 -> keyword="1"
                R.id.keyword4 -> keyword="1"
                R.id.keyword5 -> keyword="1"
                R.id.keyword6 -> keyword="1"
                R.id.keyword7 -> keyword="1"
                R.id.keyword8 -> keyword="1"
                R.id.keyword9-> keyword="1"
                R.id.keyword10 -> keyword="1"
                R.id.keyword11 -> keyword="1"
                R.id.keyword12 -> keyword="1"
                R.id.keyword13 -> keyword="1"
                R.id.keyword14 -> keyword="1"
                R.id.keyword15 -> keyword="1"

            }
        }
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage: ImageView =itemView.findViewById(R.id.friend_image)
        private val friendLevelImage: ImageView =itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName: TextView =itemView.findViewById(R.id.friend_name)
        private val friendIntro: TextView =itemView.findViewById(R.id.friend_intro)

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
            friendIntro.text=this.friend.intro
            when (this.friend.imageNum) {
                0 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> friendProfileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }

            itemView.setOnClickListener{
                //todo 친구 프로필 화면 수정
                val userProfile = Intent(context, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.friend.friendId)
                startActivity(userProfile)
            }
        }
    }
    inner class MyAdapter(private val list:List<Friend>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_friendlist, parent, false)
            return MyViewHolder(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }

    //------------------------------API 연결------------------------------------

    private fun searchFriend(search:String){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.searchFriend(memberId = myID, search = search) ?:return

        call.enqueue(object : Callback<List<Friend>> {
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "친구 검색 - 응답 성공 / t : ${response.body().toString()}")
                if(response.body()!=null){
                    val adapter=MyAdapter(response.body()!!)
                    recyclerView=binding.recyclerView
                    recyclerView.layoutManager= LinearLayoutManager(context)
                    recyclerView.adapter=adapter
                }
            }
            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "친구 검색 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()

        val mainAct=activity as MainActivity
        mainAct.HideBottomNavi(false)
    }
}