package com.by.pawsitive.main_files.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.by.pawsitive.R
import com.by.pawsitive.db.Response
import com.by.pawsitive.db.viewmodels.AuthViewModel
import com.by.pawsitive.main_files.activities.MainActivity
import kotlinx.coroutines.flow.collect
import pl.droidsonroids.gif.GifImageView

class Register2 : Fragment() {

    private lateinit var ownerNameET: EditText
    private lateinit var phoneET: EditText
    private lateinit var passwordET: EditText

    private var isAllRight = true

    private lateinit var whiteView: View
    private lateinit var loader: GifImageView

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register2, container, false)

        ownerNameET = view.findViewById(R.id.name_register2)
        phoneET = view.findViewById(R.id.phone_number_register2)
        passwordET = view.findViewById(R.id.password_register2)

        whiteView = view.findViewById(R.id.whiteView_register2)
        loader = view.findViewById(R.id.loader_register2)

        ownerNameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (ownerNameET.text!!.isEmpty()) {
                    ownerNameET.hint = "Enter your name"
                }
            } else {
                ownerNameET.hint = null
            }
        }

        phoneET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (phoneET.text!!.isEmpty()) {
                    phoneET.hint = "Enter your phone number"
                }
            } else {
                phoneET.hint = null
            }
        }

        passwordET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (passwordET.text!!.isEmpty()) {
                    passwordET.hint = "Enter your city"
                }
            } else {
                passwordET.hint = null
            }
        }

        val registerButton: Button = view.findViewById(R.id.btn_register_register2)
        registerButton.setOnClickListener {
            if (checkDetails() && checkForInternet()) {
                val petName = arguments?.getString("petName") ?: ""
                val petAge = arguments?.getString("petAge") ?: ""
                val petGender = arguments?.getString("petGender") ?: ""
                val species = arguments?.getString("species") ?: ""
                val ownerName = ownerNameET.text.toString()
                val phone = phoneET.text.toString()
                val password = passwordET.text.toString()

                authViewModel.register(
                    phone,
                    password,
                    ownerName,
                    petName,
                    petAge,
                    petGender,
                    species
                )
                observeRegisterResponse()
            }
        }

        return view
    }

    private fun checkDetails(): Boolean {

        if(ownerNameET.text.isNullOrEmpty()) {
            ownerNameET.error = "Enter your name"
            isAllRight = false
        }

        if(phoneET.text.isNullOrEmpty()) {
            phoneET.error = "Enter your phone number"
            isAllRight = false
        }

        if(phoneET.text.toString().length!=10){
            phoneET.error="Enter a valid phone number"
            isAllRight=false
        }

        if(passwordET.text.isNullOrEmpty()) {
            passwordET.error = "Enter your password"
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

    private fun observeRegisterResponse() {
        lifecycleScope.launchWhenStarted {
            authViewModel.registerResponse.collect { response ->
                when (response) {
                    is Response.Success -> {
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        Toast.makeText(requireContext(), "Register succeed", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    is Response.Failure -> {
                        Toast.makeText(requireContext(), "Register failed", Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        ownerNameET.isEnabled = true
                        phoneET.isEnabled = true
                        passwordET.isEnabled = true
                    }
                    is Response.Loading -> {
                        whiteView.visibility = View.VISIBLE
                        loader.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Register loading...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
