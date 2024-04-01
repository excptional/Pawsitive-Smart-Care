package com.by.pawsitive.main_files.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.by.pawsitive.R

class SignIn : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val registerTV: TextView = view.findViewById(R.id.register)
        registerTV.setOnClickListener {
            val navController = view.findNavController()
            navController.navigate(R.id.nav_register1)
        }

        return view
    }

}