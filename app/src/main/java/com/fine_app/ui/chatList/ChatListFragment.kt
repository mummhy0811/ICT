package com.fine_app.ui.chatList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.Post
import com.fine_app.R
import com.fine_app.databinding.FragmentChatlistBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

class ChatListFragment : Fragment() {

    private val TAG = "ChatListFragment"

    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    private val roomId = 2L
    private val memberId = 3L


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentChatlistBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, "ws://" + "10.0.2.2"
                    + ":" + "8080" + "/ws-fine" + "/websocket"
        )
        resetSubscriptions()
        return root
    }

    fun disconnectStomp(view: View?) {
        mStompClient!!.disconnect()
    }

    fun connectStomp() {
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle() //note 커넥트 여부 확인
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> toast("Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            TAG,
                            "Stomp connection error",
                            lifecycleEvent.exception
                        )
                        toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> toast("Stomp connection closed")
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> toast("Stomp failed server heartbeat")
                }
            }
        compositeDisposable!!.add(dispLifecycle)
        val dispTopic =
            mStompClient!!.topic("/sub/message/$roomId") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    Log.d(
                        TAG,
                        "Received $topicMessage"
                    )
                }
                ) { throwable: Throwable? ->
                    Log.e(
                        TAG,
                        "Error on subscribe topic",
                        throwable
                    )
                }

        // 간소화한 버전
//        mStompClient.topic("/sub/message" + roomId)
//                .subscribe(); //note 구독이 되면 정상적으로 실행 되는 상태
        compositeDisposable!!.add(dispTopic)
        mStompClient!!.connect()
    }



    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
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