package com.fine_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.fine_app.databinding.ActivityMainBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.chatList.ChatRoom
import com.fine_app.ui.home.FriendRecommendFragment
import com.fine_app.ui.home.HomeFragment
import com.fine_app.ui.myPage.LoginActivity
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

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private var userId by Delegates.notNull<Long>()
    lateinit var userInfo: SharedPreferences
    /*
    var mStompClient: StompClient? = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
    )
    private var compositeDisposable: CompositeDisposable? = null
    private val TAG = "MainActivity"

     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInfo = getSharedPreferences("userInfo", 0)
        userInfo.edit().putString("userInfo", "0").apply()
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        //loadChatList()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 안 했을 때
        if (userId == 0.toLong()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val navView: BottomNavigationView = binding.navView


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_chatList, R.id.navigation_chatList, R.id.navigation_community, R.id.navigation_myPage
            )
        )

        navView.setupWithNavController(navController)

    }
    /*
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
    }
    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }
    private fun loadChatList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.loadChatList(memberId = 3.toLong()) ?:return

        call.enqueue(object : Callback<List<Long>> {
            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                Log.d("retrofit", "로그인_채팅 호출 - 응답 성공 / t : ${response.raw()}")
                val chatList: List<Long>? = response.body()
                connectStomp()
                Log.d("ㅇㅇㅇ","${chatList?.size}")
                if(chatList?.size != 0 && chatList?.size != null){ //note 방 하나하나에 topic 연결
                    for(i in 0 until chatList!!.size){
                        val roomId=chatList[i]
                        Log.d("아아아", "${roomId}")
                        val dispTopic =
                            mStompClient!!.topic("/sub/message/$roomId") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ topicMessage: StompMessage ->
                                    val text= topicMessage.payload //todo 이 text를 recyclerView에 입히기
                                    val roomId= JSONObject(text).getString("roomId")
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
                Log.d("dkdkdk", "메인 연결 ${mStompClient?.isConnected} ${compositeDisposable}")

            }
            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                Log.d("retrofit", "로그인_채팅 호출  - 응답 실패 / t: $t")
            }
        })
    }
    private fun toast(text: String?) {
        Log.i(TAG, text!!)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

     */
    override fun onResume() {
        super.onResume()
        userId = userInfo.getString("userInfo", "2")!!.toLong()
    }


}
