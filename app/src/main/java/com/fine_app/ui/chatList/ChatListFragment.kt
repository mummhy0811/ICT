package com.fine_app.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post

        fun bind(post: Post) {
            itemView.setOnClickListener{
            }
        }
    }
    inner class MyAdapter(val list:ArrayList<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_chatlist, parent, false)
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