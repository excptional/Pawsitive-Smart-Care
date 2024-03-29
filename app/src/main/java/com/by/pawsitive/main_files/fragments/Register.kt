package com.by.pawsitive.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.by.pawsitive.R
import com.by.pawsitive.db.Response
import com.by.pawsitive.db.viewmodels.AuthViewModel

class Register : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        return view
    }

    private fun register(
        phone: String,
        password: String,
        ownerName: String,
        petName: String,
        petAge: String,
        petGender: String,
        species: String
    ) {
        authViewModel.register(phone, password, ownerName, petName, petAge, petGender, species)


        lifecycleScope.launchWhenStarted {
            authViewModel.registerResponse.collect { response ->
                when (response) {
                    is Response.Success -> {
                        Toast.makeText(requireContext(), "Register succeed", Toast.LENGTH_SHORT).show()
                        // Move to main activity using intent
                    }
                    is Response.Failure -> {
                        // User not signed in, navigate to the register page
                        Toast.makeText(requireContext(), "Register failed", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Loading -> {
                        Toast.makeText(requireContext(), "Register loading...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}