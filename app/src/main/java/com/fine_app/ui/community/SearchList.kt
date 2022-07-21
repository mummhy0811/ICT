package com.fine_app.ui.community

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
import com.fine_app.databinding.CommunitySearchBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchList : AppCompatActivity() {
    private lateinit var binding: CommunitySearchBinding
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CommunitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var text=intent.getStringExtra("text")
        binding.searchView.setQuery(text, true)

        recyclerView=binding.recyclerView
        recyclerView.layoutManager= LinearLayoutManager(this)

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //검색 버튼 눌렀을 때 호출
            override fun onQueryTextSubmit(p0: String): Boolean {
                //todo 검색 api 호출
                return true
            }
            //텍스트 입력과 동시에 호출
            override fun onQueryTextChange(p0: String): Boolean {

                return true
            }
        })
        binding.cancelButton.setOnClickListener{
            finish()
        }

    }
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var post: GroupPost
        private val image: ImageView =itemView.findViewById(R.id.writer_image)
        private val name: TextView =itemView.findViewById(R.id.writer_name)
        private val level: ImageView =itemView.findViewById(R.id.levelImage)
        private val accept: Button =itemView.findViewById(R.id.acceptButton)

        fun bind(post: GroupPost, position: Int) {
            this.post=post
            name.text=this.post.commentCount


        }
    }
    inner class MyAdapter(val list:List<GroupPost> /*todo api 데이터 형태 확인 필요*/ ): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view=layoutInflater.inflate(R.layout.item_waitlist, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post, position)
        }
    }

    //------------------------------API 연결------------------------------------


}