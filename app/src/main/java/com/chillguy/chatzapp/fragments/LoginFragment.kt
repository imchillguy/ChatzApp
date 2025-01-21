package com.chillguy.chatzapp.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.databinding.FragmentLoginBinding
import com.chillguy.chatzapp.extension.ViewExtension.hide
import com.chillguy.chatzapp.extension.ViewExtension.show
import com.chillguy.chatzapp.listener.ISaveTokenListener
import com.chillguy.chatzapp.model.response.LoginResponse
import com.chillguy.chatzapp.sessionmanager.SessionManager
import com.chillguy.chatzapp.utils.Utils
import com.chillguy.chatzapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var saveTokenListener: ISaveTokenListener

    companion object {

        fun newInstance(bundle: Bundle?): LoginFragment {
            return LoginFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        saveTokenListener = requireActivity() as ISaveTokenListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.loginViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        arguments?.let { bundle ->
            val isSignUpSuccess = bundle.getBoolean(Constants.SIGN_UP_SUCCESS, false)
            if (isSignUpSuccess) {
                val snackBar = Snackbar.make(binding.root, bundle.getString(Constants.SIGN_UP_MESSAGE, ""), Snackbar.LENGTH_LONG)
                snackBar.show()
            }
        }

        with(binding) {
            createNewAccount.setOnClickListener {
                navigateToSignUpFragment()
            }

            loginBtn.setOnClickListener {
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

                if (isValidEmail && isPasswordValid) {
                    viewModel.loginUser()
                }
            }
        }
    }

    private fun navigateToSignUpFragment() {
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, SignUpFragment())
                .commit()
        }
    }

    private fun observeLiveData(){
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is LoginResponse.Loading -> {
                    binding.progressBar.show()
                    binding.loginBtn.hide()
                }
                is LoginResponse.Success -> {
                    binding.progressBar.hide()
                    binding.loginBtn.show()
                    saveTokenListener.saveTokenToServer()
                    navigateToHomeFragment()
                }
                is LoginResponse.Error -> {
                    binding.progressBar.hide()
                    binding.loginBtn.show()
                    Toast.makeText(activity, response.errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToHomeFragment() {
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, HomeFragment())
                .commit()
        }
    }
}