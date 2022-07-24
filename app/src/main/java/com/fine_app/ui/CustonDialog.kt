package com.fine_app.ui.community

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fine_app.databinding.DialogLayoutBinding

class CustomDialog(val text:String, context: Context, Interface: CustomDialogInterface): DialogFragment() {
    private var _binding: DialogLayoutBinding? = null
    private val binding get() = _binding!!
    private var customDialogInterface: CustomDialogInterface = Interface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.textView3.text=text

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.btnYes.setOnClickListener {
            customDialogInterface.onYesButtonClicked()
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            customDialogInterface.onCancelButtonClicked()
            dismiss()
        }
        return view
    }
    interface CustomDialogInterface {
        fun onYesButtonClicked()

        fun onCancelButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}