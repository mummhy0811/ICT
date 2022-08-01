package com.fine_app.ui.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.CommunityWaitinglistBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class WaitingList : AppCompatActivity() {
    private lateinit var binding: CommunityWaitinglistBinding
    private lateinit var memberRecyclerView: RecyclerView
    private lateinit var waitingRecyclerView: RecyclerView
    private var postingID by Delegates.notNull<Long>()
    private val myID:Long=1 //todo 내 id 가져오기
    private lateinit var recruitingList:ArrayList<Recruit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunityWaitinglistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            finish()
        }
        postingID=intent.getLongExtra("postingID", 0)
        viewPosting(postingID, myID)
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var crew: Recruit
        private val image: ImageView =itemView.findViewById(R.id.writer_image)
        private val name: TextView =itemView.findViewById(R.id.writer_name)
        private val level: ImageView =itemView.findViewById(R.id.levelImage) //todo 참여자 레벨
        private val button: Button =itemView.findViewById(R.id.acceptButton)

        fun bind(crew: Recruit, position: Int) {
            this.crew=crew
            name.text=this.crew.member.nickname
            //image.setImageResource(R.drawable.ic_sprout)
            //image.setImageResource(this.crew.capacity)
            //level.setImageResource(this.crew.capacity)
            button.setOnClickListener{
                if(this.crew.acceptCheck) manageJoinGroup(postingID, recruitingList[position].recruitingId, AcceptCheck(false))
                else manageJoinGroup(postingID, recruitingList[position].recruitingId, AcceptCheck(true))
                onResume()
            }
            image.setOnClickListener{
                val userProfile = Intent(this@WaitingList, ShowUserProfileActivity::class.java)
                userProfile.putExtra("memberId",this.crew.member.memberId )
                startActivity(userProfile)
            }
        }
    }
    inner class WaitingAdapter(val list:List<Recruit> ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_waitlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val crew=list[position]
            if(!crew.acceptCheck){
                holder.bind(crew, position)
            }
        }
    }
    inner class MemberAdapter(val list:List<Recruit> ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_membertlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val crew=list[position]
            if(crew.acceptCheck){
                holder.bind(crew, position)
            }
        }
    }

    //------------------------------API 연결------------------------------------
    private fun viewPosting(postingId:Long, memberId: Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPosting(postingId = postingId , memberId=memberId ) ?:return

        call.enqueue(object :Callback<Post>{

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "그룹 커뮤니티 대기 리스트 - 응답 성공 / t : ${response.raw()}")
                recruitingList=response.body()!!.recruitingList
                val adapter1=MemberAdapter(recruitingList)
                memberRecyclerView=binding.memberRecyclerView
                memberRecyclerView.layoutManager= LinearLayoutManager(this@WaitingList)
                memberRecyclerView.adapter=adapter1

                val adapter2=WaitingAdapter(recruitingList)
                waitingRecyclerView=binding.waitingRecyclerView
                waitingRecyclerView.layoutManager= LinearLayoutManager(this@WaitingList)
                waitingRecyclerView.adapter=adapter2

                if(response.body()!!.closingCheck == true){
                    binding.closingButton.text="마감 취소"
                    waitingRecyclerView.visibility=View.INVISIBLE
                    binding.textView5.visibility=View.INVISIBLE
                    binding.view15.visibility=View.INVISIBLE
                }else if(response.body()!!.closingCheck == false) {
                    binding.closingButton.text="글 마감"
                    waitingRecyclerView.visibility=View.VISIBLE
                    binding.textView5.visibility=View.VISIBLE
                    binding.view15.visibility=View.VISIBLE
                }

                binding.closingButton.setOnClickListener{
                    editClosing(postingID)
                }
            }
            //응답실패
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "그룹 커뮤니티 세부 글 - 응답 실패 / t: $t")
            }
        })
    }
    private fun manageJoinGroup(postingId:Long?, recruitingId:Long, acceptCheck: AcceptCheck){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.manageJoinGroup(postingId = term, recruitingId=recruitingId, acceptCheck=acceptCheck) ?:return

        call.enqueue(object : Callback<Join> {
            override fun onResponse(call: Call<Join>, response: Response<Join>) {
                Log.d("retrofit", "참여 수락 변경 - 응답 성공 / t : ${response.raw()}")
                onResume()
            }
            override fun onFailure(call: Call<Join>, t: Throwable) {
                Log.d("retrofit", "참여 수락 변경 - 응답 실패 / t: $t")
            }
        })
    }
    private fun editClosing(postingId:Long?){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val term:Long= postingId ?:0
        val call = iRetrofit?.editClosing(postingId = term) ?:return

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("retrofit", "글 마감 변경 - 응답 성공 / t : ${response.raw()}")
                Log.d("retrofit", "글 마감 변경 - 응답 성공 / t : ${response.body()!!.closingCheck}")
                onResume()

            }
            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("retrofit", "글 마감 변경 - 응답 실패 / t: $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewPosting(intent.getLongExtra("postingID", 0), myID)
    }

}