package com.fine_app.ui

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.SendChat
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.chatList.ChatRoom
import com.google.gson.JsonObject
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

class Stomp: AppCompatActivity() {
    private var enteredRoomId =0.toLong() //방에 들어가있지 않은 상태
    private var userId by Delegates.notNull<Long>()
    var mStompClient: StompClient? = Stomp.over(
        Stomp.ConnectionProvider.OKHTTP, "ws://" + "54.209.17.39" + ":" + "8080" + "/ws-fine" + "/websocket"
    )

    private var compositeDisposable: CompositeDisposable? = null
    private val TAG = "MainActivity"

    fun runStomp(userId:Long){
        this.userId=userId
        //connectStomp()
        //loadChatList()
    }

    fun connectStomp() {
        mStompClient!!.withClientHeartbeat(1000).withServerHeartbeat(1000)
        resetSubscriptions()
        val dispLifecycle = mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d("stomp","Stomp connection opened" )
                        //toast("Stomp connection opened")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            TAG,
                            "Stomp connection error",
                            lifecycleEvent.exception
                        )
                        Log.d("stomp","Stomp connection error" )
                        //toast("Stomp connection error")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.d("stomp","Stomp connection closed" )
                        //toast("Stomp connection closed")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Log.d("stomp","Stomp failed server heartbeat" )
                        //toast("Stomp failed server heartbeat")
                    }
                }
            }
        compositeDisposable!!.add(dispLifecycle)
    }

    private fun loadChatList(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.loadChatList(memberId = userId) ?:return

        call.enqueue(object : Callback<List<Long>> {
            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                Log.d("retrofit", "로그인_채팅 호출 - 응답 성공 / t : ${response.raw()}")
                val chatList: List<Long>? = response.body()
                Log.d("ㅇㅇㅇ","${chatList?.size}")
                /*
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
                                    if(enteredRoomId==roomId.toLong()){
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

                 */
                val dispTopic =
                    mStompClient!!.topic("/sub/message/4") //note 어떤 방에 연결하겠다 -> topic 이 순간부터 수신 가능
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ topicMessage: StompMessage ->
                            val text= topicMessage.payload //todo 이 text를 recyclerView에 입히기
                            val roomId= JSONObject(text).getString("roomId")
                            Log.d("chatTest", "${ChatRoom().roomId}")
                            if(enteredRoomId==roomId.toLong()){
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
                mStompClient!!.connect()
                Log.d("dkdkdk", "메인 연결 ${mStompClient?.isConnected} ${compositeDisposable}")

            }
            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                Log.d("retrofit", "로그인_채팅 호출  - 응답 실패 / t: $t")
            }
        })
    }

    fun enter(roomId:Long, memberId:Long) { //note 방에 들어간 걸 알림  -- 채팅방 화면 보여줌
        Log.d("dkdkdk", "채팅방 입장 완료 ${roomId} , ${memberId}")
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "ENTER")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        Log.d("dkdkdk", "채팅방 입장 완료 ${jsonObject}")
        mStompClient!!.send("/pub/message", jsonObject.toString()) //note pub 송신 sub 수신
            .subscribe({
                Log.d("dkdkdk", "방 입장 완료")
                Log.d(
                    TAG,
                    "STOMP send successfully"
                )

            }
            ) { throwable: Throwable ->
                Log.d("dkdkdk", "방 입장 실패")
                Log.e(TAG, "Error send STOMP", throwable)
                Log.d("stomp", "${throwable.message}")
                //toast(throwable.message)
            }
        enteredRoomId=roomId
        Log.d("dkdkdk", "send...")
    }

    fun sendText(text:String?, roomId:Long, memberId:Long) { //note 메세지 보내는 코드
        Log.d("dkdkdk", "sendText 입장: ${text}")
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "TALK")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        jsonObject.addProperty("message", text)
        mStompClient!!.send("/pub/message", jsonObject.toString())
            .subscribe(
                {
                    Log.d("dkdkdk", "sendText 메세지 보냄: ${text}")
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.d("dkdkdk", "sendText 메세지 전송 실패: ${text}")
                Log.e(TAG, "Error send STOMP", throwable)
                Log.d("stomp", "${throwable.message}")
                //toast(throwable.message)
            }
    }

    fun exit(roomId:Long, memberId:Long) { //note 방 나갈 때 사용
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "EXIT")
        jsonObject.addProperty("roomId", roomId)
        jsonObject.addProperty("memberId", memberId)
        mStompClient!!.send("/pub/message", jsonObject.toString())
            .subscribe(
                {
                    Log.d(
                        TAG,
                        "STOMP send successfully"
                    )
                }
            ) { throwable: Throwable ->
                Log.e(TAG, "Error send STOMP", throwable)
            }
        enteredRoomId=0
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }
        compositeDisposable = CompositeDisposable()
    }

}