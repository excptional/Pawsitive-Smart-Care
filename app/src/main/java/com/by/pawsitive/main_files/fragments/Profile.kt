package com.by.pawsitive.main_files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.by.pawsitive.R
import com.by.pawsitive.db.viewmodels.AppViewModel
import com.by.pawsitive.db.viewmodels.AuthViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView

class Profile : Fragment() {

    private lateinit var nameProfile: TextView
    private lateinit var phoneProfile: TextView
    private lateinit var editProfile: AppCompatButton
    private lateinit var viewPetDetails: LinearLayout
    private lateinit var viewQR: LinearLayout
    private lateinit var viewSettings: LinearLayout
    private lateinit var viewAbout: LinearLayout
    private lateinit var signOut: LinearLayout
    private lateinit var imgProfile: CircleImageView
    private lateinit var whiteView: View
    private lateinit var errorText: TextView
    private lateinit var mainLayout: LinearLayout
    private lateinit var loaderProfile: GifImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var uploadImage: FloatingActionButton
    private lateinit var myUid: String
    private lateinit var authViewModel: AuthViewModel
    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        nameProfile = view.findViewById(R.id.name_profile)
        phoneProfile = view.findViewById(R.id.phone_profile)
        viewSettings = view.findViewById(R.id.settings_profile)
        viewQR = view.findViewById(R.id.qr_profile)
        viewPetDetails = view.findViewById(R.id.petDetails_profile)
        viewAbout = view.findViewById(R.id.about_profile)
        signOut = view.findViewById(R.id.sign_out_profile)
        imgProfile = view.findViewById(R.id.profileImage_profile)
        whiteView = view.findViewById(R.id.whiteView_profile)
        loaderProfile = view.findViewById(R.id.loader_profile)
        mainLayout = view.findViewById(R.id.main_layout_profile)
        errorText = view.findViewById(R.id.error_text_profile)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_profile)
        uploadImage = view.findViewById(R.id.upload_image)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        loadData()


        return view
    }

    @SuppressLint("CheckResult")
    private fun loadData() {

        authViewModel.fetchCurrentUserUid()

        lifecycleScope.launchWhenStarted {
            authViewModel.currentUserUid.collect { uid ->
                if (uid != null) {
                    myUid = uid
                    appViewModel.fetchUserInfo(uid)
                    appViewModel.userInfo.collect {user ->
                        nameProfile.text = user!!.ownerName
                        Glide.with(imgProfile).load(user.ownerImage)
                        phoneProfile.text = user.phone
                    }
                }
            }
        }

    }

}