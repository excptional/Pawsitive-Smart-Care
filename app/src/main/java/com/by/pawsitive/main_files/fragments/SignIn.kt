package com.by.pawsitive.main_files.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.by.pawsitive.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import pl.droidsonroids.gif.GifImageView
import java.util.concurrent.TimeUnit

class SignIn : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var phoneNumber: String
    private lateinit var verificationId: String

    private lateinit var whiteView: View
    private lateinit var loader: GifImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        auth = FirebaseAuth.getInstance()

        val phoneNumberET = view.findViewById<EditText>(R.id.phone_number_log_in)
        val buttonSignIn: Button = view.findViewById(R.id.btn_sign_in)

        val registerTV: TextView = view.findViewById(R.id.register)
        registerTV.setOnClickListener {
            val navController = view.findNavController()
            navController.navigate(R.id.register1)
        }



        whiteView = view.findViewById(R.id.whiteView_log_in)
        loader = view.findViewById(R.id.loader_log_in)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                whiteView.visibility = View.GONE
                loader.visibility = View.GONE
                Toast.makeText(context, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@SignIn.verificationId = verificationId
                val bundle = Bundle().apply {
                    putString("verificationId", verificationId)
                }
                findNavController().navigate(R.id.nav_otp,bundle)
            }

        }

        buttonSignIn.setOnClickListener {
            phoneNumber = phoneNumberET.text.toString().trim()
            if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
                val bundle = Bundle().apply {
                    putString("Mobile", phoneNumber)
                }
                whiteView.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$phoneNumber",
                    60,
                    TimeUnit.SECONDS,
                    requireActivity(),
                    callbacks
                )
            } else {
//                whiteView.visibility = View.GONE
//                loader.visibility = View.GONE
                phoneNumberET.error = "Enter valid phone number"
                Toast.makeText(context, "Enter valid phone number", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

//    fun getVerificationId(): String {
//        return verificationId
//    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                whiteView.visibility = View.GONE
                loader.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = task.result?.user
                    // Handle successful login
                    findNavController().navigate(R.id.nav_otp)
                } else {
                    // Handle failed login
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
