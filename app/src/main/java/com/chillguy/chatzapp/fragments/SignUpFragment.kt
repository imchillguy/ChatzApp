package com.chillguy.chatzapp.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.databinding.FragmentSignUpBinding
import com.chillguy.chatzapp.extension.ViewExtension.hide
import com.chillguy.chatzapp.extension.ViewExtension.show
import com.chillguy.chatzapp.model.response.SignUpResponse
import com.chillguy.chatzapp.utils.Utils
import com.chillguy.chatzapp.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.signUpViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        with(binding) {
            alreadyHaveAccount.setOnClickListener {
                navigateToLoginFragment()
            }

            signupBtn.setOnClickListener {
                val name = viewModel.name.value ?: ""
                val isValidName = name.isNotEmpty()
                if (!isValidName) {
                    nameTi.helperText = getString(R.string.name_cannot_be_empty)
                }

                val email = viewModel.email.value ?: ""
                val isValidEmail = Utils.isPatternMatches(email, Patterns.EMAIL_ADDRESS)
                if (!isValidEmail) {
                    emailTi.helperText = getString(R.string.invalid_email)
                }

                val password = viewModel.password.value ?: ""
                val isPasswordValid = password.length >= 8
                if (!isPasswordValid){
                    passwordTi.helperText = getString(R.string.password_must_be_eight_characters)
                }

                if (isValidName && isValidEmail && isPasswordValid) {
                    viewModel.registerUser()
                }
            }
        }
    }

    private fun navigateToLoginFragment(bundle: Bundle? = null) {
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, LoginFragment.newInstance(bundle))
                .commit()
        }
    }

    private fun observeLiveData(){
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is SignUpResponse.Loading -> {
                    binding.progressBar.show()
                    binding.signupBtn.hide()
                }
                is SignUpResponse.Success -> {
                    binding.progressBar.hide()
                    binding.signupBtn.show()
                    val bundle = bundleOf(
                        Constants.SIGN_UP_SUCCESS to true,
                        Constants.SIGN_UP_MESSAGE to response.successMsg
                    )
                    navigateToLoginFragment(bundle)
                }
                is SignUpResponse.Error -> {
                    binding.progressBar.hide()
                    binding.signupBtn.show()
                    Toast.makeText(activity, response.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}