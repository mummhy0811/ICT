package com.fine_app.ui.myPage

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fine_app.R
import com.fine_app.databinding.FragmentMypageBinding
import com.fine_app.ui.myPage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class MyPageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()
    lateinit var userData: Profile
    var count = -1

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.getActivity()!!.getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        // 프로필 생성
        getMyProfile()



        binding.tvManagePost.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ManagePostActivity::class.java)
                startActivity(intent)
            }
        }

        binding.box.setOnClickListener {
            activity?.let{
                val intent = Intent(context, UpdateProfileActivity::class.java)
                intent.putExtra("nickname", binding.tvNickname.text)
                intent.putExtra("intro", binding.tvIntro.text)
                intent.putExtra("keyword1", userData.keyword1)
                intent.putExtra("keyword2", userData.keyword2)
                intent.putExtra("keyword3", userData.keyword3)
                startActivity(intent)
            }
        }

        binding.tvManageBookmark.setOnClickListener() {
            activity?.let{
                val intent = Intent(context, AuthUniversityActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tvSetting.setOnClickListener() {
            activity?.let{
                val intent = Intent(context, SettingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tvManageGroup.setOnClickListener {
            activity?.let{
                val intent = Intent(context, ManageGroupActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mypageAuthUniversityIc.setOnClickListener {
            activity?.let{
                val intent = Intent(context, AuthUniversityActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mypageAuthPhoneIc.setOnClickListener {
            activity?.let{
                val intent = Intent(context, AuthPhoneActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mypageAuthLocationIc.setOnClickListener {
            activity?.let{
                val intent = Intent(context, AuthLocationActivity::class.java)
                startActivity(intent)
            }
        }

        binding.mypageKeywordUpdate.setOnClickListener {
            activity?.let{
                val intent = Intent(context, UpdateProfileActivity::class.java)
                intent.putExtra("nickname", binding.tvNickname.text)
                intent.putExtra("intro", binding.tvIntro.text)
                intent.putExtra("keyword1", userData.keyword1)
                intent.putExtra("keyword2", userData.keyword2)
                intent.putExtra("keyword3", userData.keyword3)
                startActivity(intent)
            }
        }

        return root
    }

    // Fragment 새로고침
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    fun getPopup() {
        val builder = AlertDialog.Builder(context)
        builder
            .setTitle("재인증")
            .setMessage("마지막 인증 날짜가 1달 전이에요. 다시 인증해주세요")
            .setPositiveButton("확인", null)
        builder.create()
        builder.show()
    }

    override fun onResume() {
        super.onResume()

        count++
        if (count == 1) {
            binding.box6.setVisibility(View.GONE)
            binding.mypageIconIm.setImageResource(R.drawable.ic_sprout)
            binding.mypageProfileLevel.setText("새싹")
            binding.mypageAuthUniversityDate.setText("2022.09.28")
        }
        if (count == 2) {
            binding.mypageIconIm.setImageResource(R.drawable.ic_tree)
            binding.mypageProfileLevel.setText("나무")
            binding.mypageAuthPhoneDate.setText("2022.09.28")
        }
        if (count == 3) {
            binding.mypageIconIm.setImageResource(R.drawable.ic_apple)
            binding.mypageProfileLevel.setText("열매")
            binding.mypageAuthLocationDate.setText("2022.09.28")
        }
        getMyProfile()

        // Fragment 클래스에서 사용 시
        getFragmentManager()?.let { refreshFragment(this, it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMyProfile() {
        val call: Call<Profile> = ServiceCreator.service.getMyProfile(userId)

        call.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    userData = response.body()!!
                    binding.tvNickname.setText(userData.nickname)
                    binding.mypageKeywordNickname.setText(userData.nickname)
                    binding.tvIntro.setText(userData.intro)
                    var imageResource = userData.userImageNum
                    when (imageResource) {
                        0 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> binding.mypageProfileImageIv.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }

//                    if (userData.level == null) {
//                        binding.mypageProfileLevel.setText("새싹 " + "1단계")
//                    } else {
//                        binding.mypageProfileLevel.setText("새싹 " + userData.level + "단계")
//                    }

                    // 관심분야
                    binding.mypageProfileKeyword1.setText(userData.keyword1)

                    if (userData.follower != null) {
                        binding.mypageProfileFriendNumTv.setText(userData.follower.toString())
                    } else {
                        binding.mypageProfileFriendNumTv.setText("0")

                    }

//                    if (userData.keyword3 == null) {
//                        binding.mypageAuthUniversityIc.setImageResource(R.drawable.ic_auth_add)
//                        binding.mypageAuthUniversityTv.setText("학교 인증")
//                    } else {
//                        binding.mypageAuthUniversityIc.setImageResource(R.drawable.ic_auth_check)
//                        binding.mypageAuthUniversityTv.setText(userData.keyword3)
//                        binding.mypageAuthUniversityDate.setText("2022.9.15")
//                    }
//
//                    if (userData.userPhoneNumber == null) {
//                        binding.mypageAuthPhoneIc.setImageResource(R.drawable.ic_auth_add)
//                        binding.mypageAuthPhoneTv.setText("번호 인증")
//                    } else {
//                        binding.mypageAuthPhoneIc.setImageResource(R.drawable.ic_auth_check)
//                        binding.mypageAuthPhoneTv.setText(userData.userPhoneNumber)
//                        binding.mypageAuthPhoneDate.setText("2022.9.15")
//                    }
//
//                    if (userData.keyword2 == null) {
//                        binding.mypageAuthLocationIc.setImageResource(R.drawable.ic_auth_add)
//                        binding.mypageAuthLocationTv.setText("지역 인증")
//                    } else {
//                        binding.mypageAuthLocationIc.setImageResource(R.drawable.ic_auth_check)
//                        binding.mypageAuthLocationTv.setText(userData.keyword2)
//                        binding.mypageAuthLocationDate.setText("2022.9.15")
//                    }

                } else {
                    Toast.makeText(context, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                //Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }

        })

    }



}