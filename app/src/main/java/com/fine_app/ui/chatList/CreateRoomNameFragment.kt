package com.fine_app.ui.chatList

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.fine_app.R
import com.fine_app.databinding.FragmentCreateGroupChatroomBinding
import com.fine_app.databinding.FragmentCreateRoomNameBinding

class CreateRoomNameFragment : Fragment() {

    private var _binding: FragmentCreateRoomNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRoomNameBinding.inflate(layoutInflater)
        val root: View = binding.root
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = view.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

         */
        var imageNum=11
        binding.imageButton.setOnClickListener {
            binding.imageButton.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border2)
            binding.imageButton2.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton3.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton4.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton5.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            imageNum=15
            Log.d("image", "이미지 클릭: ${imageNum}")
        }
        binding.imageButton2.setOnClickListener {
            binding.imageButton.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton2.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border2)
            binding.imageButton3.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton4.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton5.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            imageNum=14
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton3.setOnClickListener {
            binding.imageButton.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton2.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton3.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border2)
            binding.imageButton4.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton5.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            imageNum=13
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton4.setOnClickListener {
            binding.imageButton.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton2.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton3.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton4.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border2)
            binding.imageButton5.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            imageNum=12
            Log.d("image", "이미지 클릭: ${imageNum}")

        }
        binding.imageButton5.setOnClickListener {
            binding.imageButton.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton2.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton3.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton4.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border)
            binding.imageButton5.background= ContextCompat.getDrawable(requireContext(), R.drawable.box_border2)
            imageNum=11
            Log.d("image", "이미지 클릭: ${imageNum}")

        }

        var text=""
        binding.putRoomName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=p0.toString()
            }
        })
        binding.button.setOnClickListener {

            val text = text
            val image = imageNum
            setFragmentResult("text", bundleOf("text" to text))
            setFragmentResult("image", bundleOf("image" to image))

            //todo 뒤로가기 작동 확인
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.backButton2.setOnClickListener {
            //todo 뒤로가기 작동 확인
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            requireActivity().supportFragmentManager.popBackStack()
        }
        return root
    }

}