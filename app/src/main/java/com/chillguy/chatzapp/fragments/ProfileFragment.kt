package com.chillguy.chatzapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.databinding.FragmentProfileBinding
import com.chillguy.chatzapp.extension.ViewExtension.hide
import com.chillguy.chatzapp.extension.ViewExtension.show
import com.chillguy.chatzapp.model.dto.LoggedInUser
import com.chillguy.chatzapp.model.response.ProfileResponse
import com.chillguy.chatzapp.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    companion object {
        const val TAG = "ProfileFragment"

        fun newInstance(bundle: Bundle): ProfileFragment {
            return ProfileFragment().apply {
                arguments = bundle
            }
        }
    }

    private val galleryImagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.profileImage.setImageURI(uri)
            viewModel.setProfileImageUpdateUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        arguments?.let { bundle ->
            with(bundle) {
                val loggedInUser = LoggedInUser(
                    name = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME,""),
                    imageUrl = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_IMAGE_URL,""),
                    userId = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_ID,""),
                    documentId = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_DOCUMENT_ID,""),
                )
                viewModel.setLoggedInUser(loggedInUser)
                viewModel.setUserName(getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME,""),)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.profileViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {

            Glide.with(binding.root.context)
                .load(viewModel.loggedInUser.value?.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.login_icon)
                .into(profileImage)

            goBack.setOnClickListener {
                goBack()
            }

            profileImage.setOnClickListener {
                val dialogOptions = arrayOf(getString(R.string.choose_from_gallery),
                    getString(R.string.cancel))
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setTitle(getString(R.string.choose_your_profile_picture))
                dialogBuilder.setItems(dialogOptions) { dialog, item ->
                    when (dialogOptions[item]) {
                         getString(R.string.choose_from_gallery) -> {
                             chooseFromGallery()
                        }
                        getString(R.string.cancel) -> {
                            dialog.dismiss()
                        }
                    }
                }
                dialogBuilder.show()
            }

            updateProfileBtn.setOnClickListener {
                viewModel.updateProfile()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.profileUpdateResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is ProfileResponse.Loading -> {
                    binding.progressBar.show()
                    binding.updateProfileBtn.hide()
                }
                is ProfileResponse.Success -> {
                    binding.progressBar.hide()
                    binding.updateProfileBtn.show()
                    Toast.makeText(activity, response.successMsg, Toast.LENGTH_SHORT).show()
                }
                is ProfileResponse.Error -> {
                    binding.progressBar.hide()
                    binding.updateProfileBtn.show()
                    Toast.makeText(activity, response.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun chooseFromGallery() {
        galleryImagePicker.launch("image/*")
    }

    private fun goBack(){
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}