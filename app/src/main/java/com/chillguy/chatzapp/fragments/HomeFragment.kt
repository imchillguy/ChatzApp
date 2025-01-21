package com.chillguy.chatzapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.adapter.ContactsAdapter
import com.chillguy.chatzapp.adapter.RecentChatAdapter
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.databinding.FragmentHomeBinding
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.listener.IContactsChatListener
import com.chillguy.chatzapp.listener.IRecentChatListener
import com.chillguy.chatzapp.model.dto.Contacts
import com.chillguy.chatzapp.model.dto.RecentChat
import com.chillguy.chatzapp.viewmodel.HomeViewModel

class HomeFragment : Fragment(), IContactsChatListener, IRecentChatListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recentChatAdapter: RecentChatAdapter

    private val postNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Notification Permission Required", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.homeViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPostNotificationPermission()

        with (binding) {
            showContactsList()
            showRecentChatList()

            profileImage.setOnClickListener {
                val bundle = viewModel.getLoggedInUserBundle()
                navigateToProfileFragment(bundle)
            }
        }

        observeLiveData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStatus(ContactStatus.ONLINE.status)
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateStatus(ContactStatus.OFFLINE.status)
    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                postNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun FragmentHomeBinding.showRecentChatList() {
        recentChatAdapter = RecentChatAdapter(this@HomeFragment)
        homeChatRv.apply {
            adapter = recentChatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observeLiveData(){
        viewModel.loggedOut.observe(viewLifecycleOwner, Observer { logOut ->
            if (logOut) {
                navigateToLoginFragment()
            }
        })

        viewModel.loggedInUser.observe(viewLifecycleOwner, Observer {
            it?.let { user->
                Glide.with(binding.root.context)
                    .load(user.imageUrl)
                    .centerCrop()
                    .into(binding.profileImage)
            }
        })

        viewModel.contactsList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                contactsAdapter.contactsList = list
            }
        })

        viewModel.loggedInUserRecentChatList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                recentChatAdapter.recentChatList = list
            }
        })
    }

    private fun navigateToLoginFragment(){
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, LoginFragment())
                .commit()
        }
    }

    private fun navigateToProfileFragment(bundle: Bundle) {
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .add(R.id.root_container, ProfileFragment.newInstance(bundle))
                .addToBackStack(ProfileFragment.TAG)
                .commit()
        }
    }

    private fun FragmentHomeBinding.showContactsList() {
        contactsAdapter = ContactsAdapter(this@HomeFragment)

        homeContactsRv.apply {
            adapter = contactsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onContactsClick(contacts: Contacts) {
        val loggedInUserDetails = viewModel.loggedInUser.value
        val bundle = bundleOf(
            Constants.ContactConstants.CONTACT_USER_ID to contacts.userId,
            Constants.ContactConstants.CONTACT_USER_NAME to contacts.name,
            Constants.ContactConstants.CONTACT_USER_IMAGE_URL to contacts.imageUrl,
            Constants.ContactConstants.CONTACT_USER_STATUS to contacts.status.status,
            Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME to loggedInUserDetails?.name,
            Constants.LoggedInUserConstants.LOGGED_IN_USER_IMAGE_URL to loggedInUserDetails?.imageUrl,
            Constants.LoggedInUserConstants.LOGGED_IN_USER_ID to loggedInUserDetails?.userId
        )
        navigateToChatFragment(bundle)
    }

    private fun navigateToChatFragment(bundle: Bundle) {
        activity?.let { mActivity ->
            mActivity.supportFragmentManager.beginTransaction()
                .add(R.id.root_container, ChatFragment.newInstance(bundle))
                .addToBackStack(ChatFragment.TAG)
                .commit()
        }
    }

    override fun onRecentChatClicked(recentChat: RecentChat) {
        val loggedInUserDetails = viewModel.loggedInUser.value
        val bundle = bundleOf(
            Constants.ContactConstants.CONTACT_USER_ID to recentChat.receiverId,
            Constants.ContactConstants.CONTACT_USER_NAME to recentChat.friendName,
            Constants.ContactConstants.CONTACT_USER_IMAGE_URL to recentChat.friendImageUrl,
            Constants.ContactConstants.CONTACT_USER_STATUS to viewModel.getFriendsStatus(recentChat.receiverId),
            Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME to loggedInUserDetails?.name,
            Constants.LoggedInUserConstants.LOGGED_IN_USER_IMAGE_URL to loggedInUserDetails?.imageUrl,
            Constants.LoggedInUserConstants.LOGGED_IN_USER_ID to loggedInUserDetails?.userId
        )
        navigateToChatFragment(bundle)
    }

}