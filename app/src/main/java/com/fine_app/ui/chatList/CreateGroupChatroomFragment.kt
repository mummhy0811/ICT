package com.fine_app.ui.chatList

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.CreateChatRoom
import com.fine_app.Friend
import com.fine_app.GroupChat
import com.fine_app.R
import com.fine_app.databinding.FragmentCreateChatroomBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.ConfirmDialog
import com.fine_app.ui.community.ConfirmDialogInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class CreateGroupChatroomFragment : Fragment(), ConfirmDialogInterface {


    private var _binding: FragmentCreateChatroomBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var roomName: String
    private var myId by Delegates.notNull<Long>()
    private var imageNum by Delegates.notNull<Int>()
    lateinit var userInfo: SharedPreferences
    val selectionList=mutableListOf<Friend>()
    val receiverId=mutableListOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateChatroomBinding.inflate(layoutInflater)
        val root: View = binding.root
        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        viewFriendList(myId)
        binding.backButton2.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
            //todo 백버튼
        }
        binding.button.setOnClickListener{
            if(receiverId.size==0){
                val dialog = ConfirmDialog(this, "인원을 선택해주세요", 0, 1)
                dialog.show(requireFragmentManager(), "ConfirmDialog")
            }else{
                val dialog = ConfirmDialog(this, "그룹 채팅방을 개설하시겠습니까?", 0, 0)
                dialog.isCancelable = false
                dialog.show(requireFragmentManager(), "ConfirmDialog")
            }
        }
        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfile: ImageView =itemView.findViewById(R.id.friendProfile)
        private val friendNickname: TextView =itemView.findViewById(R.id.friendNickname)
        fun bind(friend: Friend){
            this.friend=friend
            friendNickname.text=this.friend.nickname
            when (this.friend.imageNum) {
                0 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                1 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                2 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                3 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                4 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                5 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                6 -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                else -> friendProfile.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
            }
        }
    }
    inner class MyAdapter(private val list:MutableList<Friend>): RecyclerView.Adapter<MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_create_chat, parent, false)
            return MyViewHolder(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }
    inner class MyViewHolder2(view:View): RecyclerView.ViewHolder(view){
        private lateinit var friend: Friend
        private val friendProfileImage: ImageView =itemView.findViewById(R.id.friend_image)
        private val friendLevelImage: ImageView =itemView.findViewById(R.id.friend_level) //todo 레벨 이미지 정해야함
        private val friendName: TextView =itemView.findViewById(R.id.friend_name)
        //private val checkBox:CheckBox=itemView.findViewById(R.id.checkBox)

        fun bind(friend: Friend){
            this.friend=friend
            friendName.text=this.friend.nickname
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

                if(selectionList.contains(this.friend)) {
                    selectionList.remove(friend)
                    receiverId.remove(friend.friendId)
                    itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }else{
                    selectionList.add(friend)
                    receiverId.add(friend.friendId)
                    itemView.setBackgroundColor(Color.parseColor("#3EC4C4C4"))
                }
                recyclerView=binding.recyclerView
                recyclerView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.visibility=View.VISIBLE
                recyclerView.adapter=MyAdapter(selectionList)
            }

        }
    }
    inner class MyAdapter2(private val list:MutableList<Friend>): RecyclerView.Adapter<MyViewHolder2>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            Log.d("retrofit", "그룹 채팅 추가 친구 목록 - 어댑터 / t :")
            val view=layoutInflater.inflate(R.layout.item_chatting_friendlist, parent, false)
            return MyViewHolder2(view)
        }
        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val friend=list[position]
            holder.bind(friend)
        }
    }
    private fun viewFriendList(memberId:Long){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewFriendList(memberId=memberId) ?:return

        call.enqueue(object : Callback<List<Friend>> {

            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                Log.d("retrofit", "그룹 채팅 추가 친구 목록 - 응답 성공 / t : ${response.raw()}")
                recyclerView2=binding.recyclerView2
                recyclerView2.layoutManager= LinearLayoutManager(context)
                recyclerView2.adapter=MyAdapter2(response.body()!!.toMutableList())
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.d("retrofit", "그룹 채팅 추가 친구 목록 - 응답 실패 / t: $t")
            }
        })
    }

    override fun onYesButtonClick(num: Int, theme: Int) {

        findNavController().navigate(R.id.action_navigation_create_group_chatroom_to_navigation_create_room_name2)

        setFragmentResultListener("text") { key, bundle ->
            roomName= bundle.getString("text")!!
        }
        setFragmentResultListener("image") { key, bundle ->
            imageNum = bundle.getInt("image")
        }
        addGroupChatRoom(GroupChat(myId,receiverId.toList(), roomName, imageNum))
        //todo 뒤로가기 작동 확인
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun addGroupChatRoom(Info: GroupChat){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.addGroupChatRoom(Info) ?:return

        call.enqueue(object : Callback<CreateChatRoom> {
            override fun onResponse(call: Call<CreateChatRoom>, response: Response<CreateChatRoom>) {
                Log.d("retrofit", "그룹 채팅방 생성 - 응답 성공 / t : ${response.raw()}")
                //todo 뒤로가기 확인
                requireActivity().supportFragmentManager.beginTransaction().remove(this@CreateGroupChatroomFragment).commit()
                requireActivity().supportFragmentManager.popBackStack()
            }
            override fun onFailure(call: Call<CreateChatRoom>, t: Throwable) {
                Log.d("retrofit", "그룹 채팅방 생성 - 응답 실패 / t: $t")
            }
        })
    }
}