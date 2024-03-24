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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.by.pawsitive.R
import com.by.pawsitive.db.viewmodels.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.droidsonroids.gif.GifImageView
import java.util.UUID

class Register1 : Fragment() {
    private lateinit var backBtn: ImageView
    private lateinit var nextBtn: AppCompatButton
    private lateinit var petNameET: AppCompatEditText
    private lateinit var speciesET: AppCompatEditText
    private lateinit var breedET: AppCompatEditText
    private lateinit var colorET: AppCompatEditText
    private lateinit var ageET: AppCompatEditText
    private lateinit var whiteView: View
    private lateinit var loader: GifImageView

    private var isAllRight = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register1, container, false)


        backBtn = view.findViewById(R.id.back_btn_register1)
        nextBtn = view.findViewById(R.id.btn_next_register1)
        petNameET = view.findViewById(R.id.pet_name_register1)
        speciesET = view.findViewById(R.id.species_register1)
        breedET = view.findViewById(R.id.breed_register1)
        colorET = view.findViewById(R.id.color_register1)
        ageET = view.findViewById(R.id.age_register1)
        whiteView = view.findViewById(R.id.whiteView_register1)
        loader = view.findViewById(R.id.loader_register1)

        backBtn.setOnClickListener{
            val navController = view.findNavController()
            navController.navigate(R.id.nav_sign_in)
        }

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

        breedET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (breedET.text!!.isEmpty()) {
                    breedET.hint = "Enter your pet's breed"
                }
            } else {
                breedET.hint = null
            }
        }

        colorET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (colorET.text!!.isEmpty()) {
                    colorET.hint = "Enter your pet's color"
                }
            } else {
                colorET.hint = null
            }
        }

        ageET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (ageET.text!!.isEmpty()) {
                    ageET.hint = "Enter your pet's age"
                }
            } else {
                ageET.hint = null
            }
        }

        nextBtn.setOnClickListener {
            isAllRight = true
            if (checkDetails()) {
                whiteView.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE
                if (checkForInternet()) {
                    petNameET.isEnabled = false
                    speciesET.isEnabled = false
                    breedET.isEnabled = false
                    colorET.isEnabled = false
                    ageET.isEnabled = false

                    val uid = UUID.randomUUID().toString()

                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("uid", uid)
                        apply()
                    }

                        val petName = petNameET.text.toString()
                        val species = speciesET.text.toString()
                        val breed = breedET.text.toString()
                        val color = colorET.text.toString()
                        val age = ageET.text.toString()

                        val db = FirebaseFirestore.getInstance()

                        val pet = hashMapOf(
                            "petName" to petName,
                            "species" to species,
                            "breed" to breed,
                            "color" to color,
                            "age" to age
                        )

                        db.collection("users").document(uid).collection("pet")
                            .add(pet)
                            .addOnSuccessListener { documentReference ->
                                val navController = view.findNavController()
                                navController.navigate(R.id.register2)

                                whiteView.visibility = View.GONE
                                loader.visibility = View.GONE
                                petNameET.isEnabled = true
                                breedET.isEnabled = true
                                speciesET.isEnabled = true
                                colorET.isEnabled = true
                                ageET.isEnabled = true
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to add pet: $e", Toast.LENGTH_SHORT).show()
                                whiteView.visibility = View.GONE
                                loader.visibility = View.GONE
                                petNameET.isEnabled = true
                                breedET.isEnabled = true
                                speciesET.isEnabled = true
                                colorET.isEnabled = true
                                ageET.isEnabled = true
                            }
                    } else {
                        Toast.makeText(requireContext(), "User UID not found", Toast.LENGTH_SHORT).show()
                        whiteView.visibility = View.GONE
                        loader.visibility = View.GONE
                        petNameET.isEnabled = true
                        breedET.isEnabled = true
                        speciesET.isEnabled = true
                        colorET.isEnabled = true
                        ageET.isEnabled = true
                    }
                } else {
                    whiteView.visibility = View.GONE
                    loader.visibility = View.GONE
                    petNameET.isEnabled = true
                    breedET.isEnabled = true
                    speciesET.isEnabled = true
                    colorET.isEnabled = true
                    ageET.isEnabled = true
                    Toast.makeText(requireContext(), "Something went wrong!\nCheck your internet connection", Toast.LENGTH_SHORT).show()
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

        if(breedET.text.isNullOrEmpty()) {
            breedET.error = "Enter your pet's breed"
            isAllRight = false
        }

        if(colorET.text.isNullOrEmpty()) {
            colorET.error = "Enter your pet's color"
            isAllRight = false
        }

        if(ageET.text.isNullOrEmpty()) {
            ageET.error = "Enter your pet's age"
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

}