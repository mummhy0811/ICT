package com.fine_app

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Post(
    val postingId:Long,
    val memberId: Long,
    val nickname: String,
    val title:String,
    val content: String,
    val commentCount:String,
    val createdDate:String,
    val lastModifiedDate:String,
    val closingCheck:Boolean,
    val groupCheck:Boolean,
    @SerializedName(value = "maxMember")
    val capacity:Int,
    val comments: ArrayList<Comment>
):Serializable

data class GroupPost(
    val postingId:Long,
    val memberId: Long,
    val nickname: String,
    val title:String,
    val content: String,
    val commentCount:String,
    val createdDate:String,
    val lastModifiedDate:String,
    val closingCheck:Boolean,
    val groupCheck:Boolean,
    @SerializedName(value = "maxMember")
    val capacity:Int,
    val comments: ArrayList<Comment>,
    val recruitingList:ArrayList<Recruit>
):Serializable

data class Recruit(
    val id:Long,
    val member:Member,
    val accept_check:Boolean
):Serializable

data class Comment(
    val memberId:Long, //댓글 단 사람
    val postingId:Long, //댓글 달린 글 아이디
    val text:String, //댓글 내용
    val commentId:Long
    ):Serializable

data class Member (
    val id: Long,
    val nickname: String,
    val intro:String,
    val password:String,
    val email:String,
    val userIntroduction:String,
    val userUniversity:String,
    val userCollege:String,
    val userPhoneNumber: String,
    val userResidence:String,
    val level:String,
    val report:Long
)

data class Posting(
    val title:String,
    val content:String,
    val groupCheck : Boolean
)

data class GroupPosting(
    val title:String,
    val content:String,
    val groupCheck : Boolean,
    val maxMember:Int
)

data class BookMark(
    val memberId:Long,
    val postingId:Long,
    val bookmarkId:Long
)

data class Join(
    val id:Long,
    val accept_check:Boolean
)
