package com.fine_app.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.databinding.FragmentHomeBinding
import com.fine_app.ui.community.PostDetail_Main
import kotlin.ClassCastException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    var activeCallback:ButtonListener?=null
    interface ButtonListener{
        fun onButtonClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //테스트용 데이터
        val post1=Post(1, 1, "user1", "testTitle", "강의 정해서\n개강 전까지 같이 공부하실 분?\n연락주세요", "0",
            "123456", " ", false, false, 123, 5, 5,3, ArrayList<Comment>(), ArrayList<Recruit>())
        val post2=Post(1, 1, "user2", "testTitle2", "가나다라 \n마바사\n아자차카타\n파하", "0",
            "123456", " ", false, false, 123, 5, 5,3, ArrayList<Comment>(), ArrayList<Recruit>())
        val testList=ArrayList<Post>()

        testList.add(post1)
        testList.add(post2)

        recyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter=MyAdapter(testList)

        val items = arrayOf("3개 일치", "2개 일치", "1개 일치")
        binding.spinner2.adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                when (position) {
                    // todo 1개 일치 api
                    // todo 2개 일치 api
                    // todo 3개 일치 api
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.showButton.setOnClickListener {
            //todo 친구 추천 프래그먼트 연결
            Log.d("home", "친구 매칭 프래그먼트 이동")
            activeCallback?.onButtonClick()
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            if(context is ButtonListener){
                activeCallback=context
            }
        } catch (e:ClassCastException){
            throw ClassCastException(context.toString()+" must implement button Listener.")
        }
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val recommendTitle:TextView =itemView.findViewById(R.id.recommendTitle)
        private val recommendContent: TextView =itemView.findViewById(R.id.recommendContent)
        private val recommendCapacity: TextView =itemView.findViewById(R.id.recommendCapacity)
        private val recommendVacancy: TextView =itemView.findViewById(R.id.recommendVacancy)
        //private val recommendKeyWord3: TextView =itemView.findViewById(R.id.recommendKeyWord3)
        //private val recommendKeyWord2: TextView =itemView.findViewById(R.id.recommendKeyWord2)
        //private val recommendKeyWord1: TextView =itemView.findViewById(R.id.recommendKeyWord1)

        fun bind(post: Post) {
            this.post=post
            recommendTitle.text=this.post.title
            recommendContent.text=this.post.content
            recommendCapacity.text=this.post.capacity.toString()
            recommendVacancy.text=(this.post.capacity - this.post.participants).toString()

            itemView.setOnClickListener{
                val postDetail= Intent(activity, PostDetail_Main::class.java)
                postDetail.putExtra("postingId", this.post.postingId)
                postDetail.putExtra("memberId", this.post.memberId)
                startActivity(postDetail)
            }
        }
    }
    inner class MyAdapter(val list:ArrayList<Post>): RecyclerView.Adapter<MyViewHolder>() {
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}