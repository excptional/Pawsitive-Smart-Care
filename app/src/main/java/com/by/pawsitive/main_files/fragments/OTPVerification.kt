package com.by.pawsitive.main_files.fragments

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.by.pawsitive.R
import com.by.pawsitive.main_files.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import pl.droidsonroids.gif.GifImageView

class OTPVerification : Fragment() {
    private lateinit var otpEditText1: EditText
    private lateinit var otpEditText2: EditText
    private lateinit var otpEditText3: EditText
    private lateinit var otpEditText4: EditText
    private lateinit var otpEditText5: EditText
    private lateinit var otpEditText6: EditText

    private lateinit var submitButton: Button

    private lateinit var verificationId: String


    private lateinit var auth: FirebaseAuth

    private lateinit var whiteView: View
    private lateinit var loader: GifImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_o_t_p_verification, container, false)

        auth = FirebaseAuth.getInstance()

        otpEditText1 = view.findViewById(R.id.otp_1)
        otpEditText2 = view.findViewById(R.id.otp_2)
        otpEditText3 = view.findViewById(R.id.otp_3)
        otpEditText4 = view.findViewById(R.id.otp_4)
        otpEditText5 = view.findViewById(R.id.otp_5)
        otpEditText6 = view.findViewById(R.id.otp_6)

        submitButton = view.findViewById(R.id.btn_submit_o_t_p)

        whiteView = view.findViewById(R.id.whiteView_log_in)
        loader = view.findViewById(R.id.loader_log_in)

        val args = arguments
        verificationId = args?.getString("verificationId") ?: ""

        // Add focus management for OTP input fields
        val editTexts = listOf(otpEditText1, otpEditText2, otpEditText3, otpEditText4, otpEditText5, otpEditText6)

        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Move focus to the next EditText when a number is entered
                    if (s?.length == 1) {
                        if (index < editTexts.lastIndex) {
                            editTexts[index + 1].requestFocus()
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                // Delete the number and move focus to the previous EditText when backspace is pressed
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (index > 0) {
                        val previousEditText = editTexts[index - 1]
                        previousEditText.requestFocus()
                        val presentEditText=editTexts[index]
                        presentEditText.text?.clear()
                    }
                    return@setOnKeyListener true
                }
                false
            }

        }

        submitButton.setOnClickListener {
            val otp1 = otpEditText1.text.toString().trim()
            val otp2 = otpEditText2.text.toString().trim()
            val otp3 = otpEditText3.text.toString().trim()
            val otp4 = otpEditText4.text.toString().trim()
            val otp5 = otpEditText5.text.toString().trim()
            val otp6 = otpEditText6.text.toString().trim()

            val otp = "$otp1$otp2$otp3$otp4$otp5$otp6"

            if (otp.isNotEmpty() && otp.length == 6) {
                whiteView.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(context, "Enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                whiteView.visibility = View.GONE
                loader.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Handle failed login
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
