//package com.by.pawsitive.main_files.fragments
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import com.by.pawsitive.R
//import com.by.pawsitive.db.Response
//import com.by.pawsitive.db.viewmodels.AuthViewModel
//
//class Register1 : Fragment() {
//
//    private val authViewModel: AuthViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_register1, container, false)
//
//        return view
//    }
//
//    private fun register(
//        phone: String,
//        password: String,
//        ownerName: String,
//        petName: String,
//        petAge: String,
//        petGender: String,
//        species: String
//    ) {
//        authViewModel.register(phone, password, ownerName, petName, petAge, petGender, species)
//
//
//        lifecycleScope.launchWhenStarted {
//            authViewModel.registerResponse.collect { response ->
//                when (response) {
//                    is Response.Success -> {
//                        Toast.makeText(requireContext(), "Register succeed", Toast.LENGTH_SHORT).show()
//                        // Move to main activity using intent
//                    }
//                    is Response.Failure -> {
//                        // User not signed in, navigate to the register page
//                        Toast.makeText(requireContext(), "Register failed", Toast.LENGTH_SHORT).show()
//                    }
//                    is Response.Loading -> {
//                        Toast.makeText(requireContext(), "Register loading...", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }
//
//}

package com.by.pawsitive.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.by.pawsitive.R
import com.by.pawsitive.db.viewmodels.AuthViewModel

class Register1 : Fragment() {

    private lateinit var petNameET: EditText
    private lateinit var petAgeET: EditText
    private lateinit var petGenderET: EditText
    private lateinit var speciesET: EditText
    private lateinit var nextBtn: AppCompatButton
    private var isAllRight = true

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register1, container, false)

        petNameET = view.findViewById(R.id.pet_name_register1)
        petAgeET = view.findViewById(R.id.pet_age_register1)
        petGenderET = view.findViewById(R.id.pet_gender_register1)
        speciesET = view.findViewById(R.id.species_register1)

        petNameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (petNameET.text!!.isEmpty()) {
                    petNameET.hint = "Enter your pet's name"
                }
            } else {
                petNameET.hint = null
            }
        }

        speciesET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (speciesET.text!!.isEmpty()) {
                    speciesET.hint = "Enter your pet's species"
                }
            } else {
                speciesET.hint = null
            }
        }

        petGenderET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (petGenderET.text!!.isEmpty()) {
                    petGenderET.hint = "Enter your pet's gender"
                }
            } else {
                petGenderET.hint = null
            }
        }

        petAgeET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (petAgeET.text!!.isEmpty()) {
                    petAgeET.hint = "Enter your pet's age"
                }
            } else {
                petAgeET.hint = null
            }
        }

        nextBtn = view.findViewById(R.id.btn_next_register1)
        nextBtn.setOnClickListener {
            if (checkDetails()) {
                val bundle = Bundle().apply {
                    putString("petName", petNameET.text.toString())
                    putString("petAge", petAgeET.text.toString())
                    putString("petGender", petGenderET.text.toString())
                    putString("species", speciesET.text.toString())
                }
                findNavController().navigate(R.id.nav_register2, bundle)
            }
        }

        return view
    }

    private fun checkDetails(): Boolean {

        if(petNameET.text.isNullOrEmpty()) {
            petNameET.error = "Enter your pet's name"
            isAllRight = false
        }

        if(speciesET.text.isNullOrEmpty()) {
            speciesET.error = "Enter your pet's species"
            isAllRight = false
        }


        if(petGenderET.text.isNullOrEmpty()) {
            petGenderET.error = "Enter your pet's gender"
            isAllRight = false
        }

        if(petAgeET.text.isNullOrEmpty()) {
            petAgeET.error = "Enter your pet's age"
            isAllRight = false
        }

        return isAllRight
    }

}
