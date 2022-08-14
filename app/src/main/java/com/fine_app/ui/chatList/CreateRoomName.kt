package com.fine_app.ui.chatList

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.fine_app.databinding.ChattingSettingBinding


class CreateRoomName: AppCompatActivity(){

    private lateinit var binding: ChattingSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingSettingBinding.inflate(layoutInflater)

        var text=""
        binding.putRoomName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                text=p0.toString()
            }
        })
        binding.button.setOnClickListener {
            val intent = Intent()
            intent.putExtra("text", text)
            setResult(RESULT_OK, intent)
            finish()
        }
        setContentView(binding.root)
    }

}