package com.fine_app.ui.myPage

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.SendChat
import com.fine_app.databinding.ActivityLoginBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.chatList.ChatRoom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var userData: Profile
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    /*
    var mStompClient: StompClient? = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
    )
    private var compositeDisposable: CompositeDisposable? = null


    private val TAG = "ChatListFragment"

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginOkBtn.setOnClickListener {
            userLogin()
        }

        binding.loginSignupTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun userLogin() {
        val requestLoginData = RequestLoginData(
            id = binding.loginIdEt.text.toString(),
            password = binding.loginPwdEt.text.toString(),
        )

        val id = binding.loginIdEt.text.toString()
        val password = binding.loginPwdEt.text.toString()

        val call: Call<Profile> = ServiceCreator.service.userLogin(requestLoginData)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    userData = response.body()!!
                    userInfo.edit().putString("userInfo", userData.id.toString()).apply()
                    //Log.d("aaaa", "${userInfo.getString("userInfo", "2")!!.toLong()}")
                    //loadChatList()
                    //com.fine_app.ui.Stomp().runStomp(userInfo.getString("userInfo", "2")!!.toLong())
                    //ChatRoom().connectStomp(userInfo.getString("userInfo", "2")!!.toLong())
                    //ChatRoom().loadChatList(userInfo.getString("userInfo", "2")!!.toLong())
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    // 로그인 실패
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })



    }
/*
    private fun loadChatList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.loadChatList(memberId = userInfo.getString("userInfo", "2")!!.toLong()) ?:return

        call.enqueue(object : Callback<List<Long>> {
            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                Log.d("retrofit", "로그인_채팅 호출 - 응답 성공 / t : ${response.raw()}")
                val chatList: List<Long>? = response.body()
                connectStomp()
                if(chatList?.size != 0){ //note 방 하나하나에 topic 연결
                    for(i in 0 until chatList!!.size){
                        val roomId=chatList[i]
                        Log.d("아아아", "${roomId}")
                        val dispTopic =
                            mStompClient!!.topic("/sub/message/$roomId") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ topicMessage: StompMessage ->
                                    val text= topicMessage.payload //todo 이 text를 recyclerView에 입히기
                                    val roomId=JSONObject(text).getString("roomId")
                                    Log.d("chatTest", "${ChatRoom().roomId}")
                                    if(ChatRoom().roomId==roomId.toLong()){
                                        val memberId:Long= JSONObject(text).getString("memberId").toLong()
                                        val nickName= JSONObject(text).getString("nickName")
                                        val message= JSONObject(text).getString("message")
                                        val unreadCount= JSONObject(text).getString("unreadCount").toInt()
                                        //val time= JSONObject(text).getString("createdTime")
                                        //val imageNum=JSONObject(text).getString("imageNum").toInt() //todo 이미지넘버 확인
                                        val newMessage= SendChat(memberId, nickName, 1, message, unreadCount, "2022-08-18T17:24:05.3960486") //todo 이미지넘버 확인
                                        Log.d("sendChat", "${newMessage}")
                                        ChatRoom().oldMessage.add(newMessage)
                                        Log.d("sendChat", "${ ChatRoom().oldMessage}")
                                        ChatRoom().recyclerView.adapter?.notifyItemInserted(ChatRoom().oldMessage.size)
                                    } //리사이클러뷰 등록 코드
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
                        compositeDisposable!!.add(dispTopic)
                    }
                }
                mStompClient!!.connect()
            }
            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                Log.d("retrofit", "로그인_채팅 호출  - 응답 실패 / t: $t")
            }
        })
    }

    fun connectStomp() {
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle()
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
        /*
        val dispTopic =
            mStompClient!!.topic("/sub/message/$roomId") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    val text= topicMessage.payload //todo 이 text를 recyclerView에 입히기
                    //val roomId=JSONObject(text).getString("roomId")
                    val memberId:Long= JSONObject(text).getString("memberId").toLong()
                    val nickName= JSONObject(text).getString("nickName")
                    val message= JSONObject(text).getString("message")
                    val unreadCount= JSONObject(text).getString("unreadCount").toInt()
                    //val time= JSONObject(text).getString("createdTime")
                    //val imageNum=JSONObject(text).getString("imageNum").toInt() //todo 이미지넘버 확인
                    val newMessage= SendChat(memberId, nickName, 1, message, unreadCount, "2022-08-18T17:24:05.3960486") //todo 이미지넘버 확인
                    Log.d("sendChat", "${newMessage}")
                    oldMessage.add(newMessage)
                    Log.d("sendChat", "${oldMessage}")
                    //MyAdapter(oldMessage).notifyItemInserted(oldMessage.size)
                    recyclerView.adapter?.notifyItemInserted(oldMessage.size)
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


        compositeDisposable!!.add(dispTopic)
        mStompClient!!.connect()

         */
    }
    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

     */
}

