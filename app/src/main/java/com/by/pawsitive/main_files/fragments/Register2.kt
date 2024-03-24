package com.by.pawsitive.main_files.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.by.pawsitive.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import pl.droidsonroids.gif.GifImageView
import java.util.UUID
import java.util.concurrent.TimeUnit

class Register2 : Fragment() {

    private lateinit var backBtn: ImageView
    private lateinit var registerBtn: AppCompatButton
    private lateinit var nameET: AppCompatEditText
    private lateinit var phoneNumberET: AppCompatEditText
    private lateinit var cityET: AppCompatEditText
    private lateinit var stateET: AppCompatEditText
    private lateinit var pinCodeET: AppCompatEditText
    private lateinit var whiteView: View
    private lateinit var loader: GifImageView

    private var isAllRight = true
    private var verificationId: String? = null

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register2, container, false)

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
                this@Register2.verificationId = verificationId
                val bundle = Bundle().apply {
                    putString("verificationId", verificationId)
                }
                findNavController().navigate(R.id.nav_otp,bundle)
            }

        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val uid = sharedPref.getString("uid", null)

        backBtn = view.findViewById(R.id.back_btn_register2)
        registerBtn = view.findViewById(R.id.btn_register_register2)
        nameET = view.findViewById(R.id.name_register2)
        phoneNumberET = view.findViewById(R.id.phone_number_register2)
        cityET = view.findViewById(R.id.city_register2)
        stateET = view.findViewById(R.id.state_register2)
        pinCodeET = view.findViewById(R.id.pin_code_register2)
        whiteView = view.findViewById(R.id.whiteView_register2)
        loader = view.findViewById(R.id.loader_register2)

        backBtn.setOnClickListener{
            val navController = view.findNavController()
            navController.navigate(R.id.register1)
        }

        nameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (nameET.text!!.isEmpty()) {
                    nameET.hint = "Enter your name"
                }
            } else {
                nameET.hint = null
            }
        }

        phoneNumberET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (phoneNumberET.text!!.isEmpty()) {
                    phoneNumberET.hint = "Enter your phone number"
                }
//                else if (phoneNumberET.text!!.length != 10) {
//                    phoneNumberET.error = "Phone number must be 10 digits"
//                    isAllRight = false
//                }
            } else {
                phoneNumberET.hint = null
            }
        }

        cityET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (cityET.text!!.isEmpty()) {
                    cityET.hint = "Enter your city"
                }
            } else {
                cityET.hint = null
            }
        }

        stateET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (stateET.text!!.isEmpty()) {
                    stateET.hint = "Enter your state"
                }
            } else {
                stateET.hint = null
            }
        }

        pinCodeET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (pinCodeET.text!!.isEmpty()) {
                    pinCodeET.hint = "Enter your pin code"
                }
            } else {
                pinCodeET.hint = null
            }
        }

        registerBtn.setOnClickListener {
            isAllRight = true
            if (checkDetails()) {
                whiteView.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE
                if (checkForInternet()) {
                    nameET.isEnabled = false
                    phoneNumberET.isEnabled = false
                    cityET.isEnabled = false
                    stateET.isEnabled = false
                    pinCodeET.isEnabled = false

                    val name = nameET.text.toString()
                    val phoneNumber = phoneNumberET.text.toString()
                    val city = cityET.text.toString()
                    val state = stateET.text.toString()
                    val pinCode = pinCodeET.text.toString()

                    val db = FirebaseFirestore.getInstance()

                    val owner = hashMapOf(
                        "name" to name,
                        "phoneNumber" to phoneNumber,
                        "city" to city,
                        "state" to state,
                        "pinCode" to pinCode
                    )

                    if (uid != null) {
                        db.collection("users").document(uid).collection("owner")
                            .add(owner)
                            .addOnSuccessListener { documentReference ->
                                val completePhoneNumber = "+91$phoneNumber"
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    completePhoneNumber,
                                    60,
                                    TimeUnit.SECONDS,
                                    requireActivity(),
                                    callbacks
                                )
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to add owner: $e", Toast.LENGTH_SHORT).show()
                                resetViews()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Something went wrong!\nCheck your internet connection", Toast.LENGTH_SHORT).show()
                    resetViews()
                }
            }
        }

        return view

    }

    private fun checkDetails(): Boolean {

        if(nameET.text.isNullOrEmpty()) {
            nameET.error = "Enter your name"
            isAllRight = false
        }

        if(phoneNumberET.text.isNullOrEmpty()) {
            phoneNumberET.error = "Enter your phone number"
            isAllRight = false
        }

        if(phoneNumberET.text.toString().length!=10){
            phoneNumberET.error="Enter a valid phone number"
            isAllRight=false
        }

        if(cityET.text.isNullOrEmpty()) {
            cityET.error = "Enter your city"
            isAllRight = false
        }

        if(stateET.text.isNullOrEmpty()) {
            stateET.error = "Enter your state"
            isAllRight = false
        }

        if(pinCodeET.text.isNullOrEmpty()) {
            pinCodeET.error = "Enter your pin code"
            isAllRight = false
        }

        return isAllRight
    }

    private fun checkForInternet(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.nav_otp)
                } else {
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                    resetViews()
                }
            }
    }

    private fun resetViews() {
        whiteView.visibility = View.GONE
        loader.visibility = View.GONE
        nameET.isEnabled = true
        phoneNumberET.isEnabled = true
        cityET.isEnabled = true
        stateET.isEnabled = true
        pinCodeET.isEnabled = true
    }

}