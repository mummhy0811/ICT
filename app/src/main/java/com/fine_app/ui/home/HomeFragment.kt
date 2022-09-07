package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.Post
import com.fine_app.databinding.FragmentHomeBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.PostDetail_Group
import com.fine_app.ui.community.PostDetail_Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView //게시물 추천
    private lateinit var recyclerView2: RecyclerView //친구 추천
    lateinit var userInfo: SharedPreferences
    private var myId by Delegates.notNull<Long>()
    lateinit var user1_name: String
    lateinit var user1_levelImage:ImageView
    lateinit var user1_profileImage:ImageView
    lateinit var user1_keyword1:String
    lateinit var user1_keyword2:String
    lateinit var user1_keyword3:String
    lateinit var user2_name: String
    lateinit var user2_levelImage:ImageView
    lateinit var user2_profileImage:ImageView
    lateinit var user2_keyword1:String
    lateinit var user2_keyword2:String
    lateinit var user2_keyword3:String
    lateinit var user3_name: String
    lateinit var user3_levelImage:ImageView
    lateinit var user3_profileImage:ImageView
    lateinit var user3_keyword1:String
    lateinit var user3_keyword2:String
    lateinit var user3_keyword3:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()

        binding.showButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_home_recommend)
        }
        /*
        val items = arrayOf("지역", "학교", "전공")
        var category=1
        val spinner: Spinner = binding.spinner4
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> category=1
                    1 -> category=2
                    2 -> category=3
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        //친구추천 테스트용 데이터
        val post1= Test("한이음", "안녕하세요\n만나서 반갑습니다아!")
        val post2= Test("한이음2", "2호선 카페투어 하실 분~!")
        val post3= Test("한이음3", "서울 신림쪽 살고 있어용 카페 같이 다녀요!")
        val post4= Test("한이음4", "ISTJ \ns대 컴퓨터공학과 입니다!! ")
        val testList=ArrayList<Test>()
        testList.add(post1)
        testList.add(post2)
        testList.add(post3)
        testList.add(post4)
        recyclerView2=binding.recyclerView2
        recyclerView2.layoutManager=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.adapter=MatchingAdapter(testList)

         */
        viewPopularPosting()
        return root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val recommendTitle:TextView =itemView.findViewById(R.id.recommendTitle)
        private val recommendContent: TextView =itemView.findViewById(R.id.recommendContent)
        private val recommendCapacity: TextView =itemView.findViewById(R.id.recommendCapacity)
        private val recommendVacancy: TextView =itemView.findViewById(R.id.recommendVacancy)
        //private val recommendKeyWord3: TextView =itemView.findViewById(R.id.recommendKeyWord3)
        //private val recommendKeyWord2: TextView =itemView.findViewById(R.id.recommendKeyWord2)
        private val recommendKeyWord1: TextView =itemView.findViewById(R.id.recommendKeyWord1)
        private val capacityBox:LinearLayout=itemView.findViewById(R.id.capacityBox)

        fun bind(post: Post) {
            this.post=post
            recommendTitle.text=this.post.title
            recommendContent.text=this.post.content
            if(this.post.groupCheck) {
                recommendKeyWord1.text="그룹"
                recommendCapacity.text=this.post.capacity.toString()
                recommendVacancy.text=(this.post.capacity - this.post.participants).toString()
                Log.d("home", "${this.post.capacity}, ${this.post.participants}, ${this.post.capacity - this.post.participants}")
            }
            else {
                recommendKeyWord1.text="개인"
                capacityBox.visibility=View.INVISIBLE
            }

            itemView.setOnClickListener{
                if(this.post.groupCheck){
                    val postDetail= Intent(activity, PostDetail_Group::class.java)
                    postDetail.putExtra("postingId", this.post.postingId)
                    postDetail.putExtra("memberId", this.post.memberId)
                    startActivity(postDetail)
                }else{
                    val postDetail= Intent(activity, PostDetail_Main::class.java)
                    postDetail.putExtra("postingId", this.post.postingId)
                    postDetail.putExtra("memberId", this.post.memberId)
                    startActivity(postDetail)
                }
            }
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_recommend, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    inner class MatchingViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var test: Test
        private val nickname: TextView =itemView.findViewById(R.id.home_matching_name)
        private val image: ImageView =itemView.findViewById(R.id.home_matching_image)
        fun bind(test: Test) {
            this.test=test
            nickname.text=this.test.name
            if(this.test.name=="한이음2") image.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            else if(this.test.name=="한이음3") image.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
            else if(this.test.name=="한이음4") image.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
            itemView.setOnClickListener{
            }
        }
    }
    inner class MatchingAdapter(val list:List<Test>): RecyclerView.Adapter<MatchingViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchingViewHolder {
            val view=layoutInflater.inflate(R.layout.item_matching_home, parent, false)
            return MatchingViewHolder(view)
        }
        override fun onBindViewHolder(holder: MatchingViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    private fun viewPopularPosting(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPopularPosting() ?:return

        call.enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "홈 - 응답 성공 / t : ${response.raw()}")
                recyclerView=binding.recyclerView
                recyclerView.layoutManager=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter=MyAdapter(response.body()!!)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "홈 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewMatchingFriends(category:Int){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMatchingFriends(myId, category = category ) ?:return

        call.enqueue(object : Callback<List<MatchingFriend>> {

            override fun onResponse(call: Call<List<MatchingFriend>>, response: Response<List<MatchingFriend>>) {
                Log.d("retrofit", "홈 친구추천 - 응답 성공 / t : ${response.raw()}")
                val friends:MatchingFriend= response.body()!![0]
                if(category==1){
                    user1_name=friends.nickname
                    user1_keyword1=friends.keyword1
                    user1_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user1_keyword3=friends. todo 유저 키워드3 추가
                }else if(category==2){
                    user2_name=friends.nickname
                    user2_keyword1=friends.keyword1
                    user2_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user2_keyword3=friends. todo 유저 키워드3 추가
                }else{
                    user3_name=friends.nickname
                    user3_keyword1=friends.keyword1
                    user3_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user3_keyword3=friends. todo 유저 키워드3 추가
                }
            }

            override fun onFailure(call: Call<List<MatchingFriend>>, t: Throwable) {
                Log.d("retrofit", "홈 친추구천 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}