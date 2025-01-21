package com.chillguy.chatzapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chillguy.chatzapp.R
import com.chillguy.chatzapp.adapter.MessageAdapter
import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.databinding.FragmentChatBinding
import com.chillguy.chatzapp.enums.ContactStatus
import com.chillguy.chatzapp.mapper.ContactsMapper.toContactsStatus
import com.chillguy.chatzapp.model.dto.Contacts
import com.chillguy.chatzapp.model.dto.Message
import com.chillguy.chatzapp.model.dto.LoggedInUser
import com.chillguy.chatzapp.viewmodel.ChatViewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter

    companion object {
        const val TAG = "ChatFragment"

        fun newInstance(bundle: Bundle): ChatFragment {
            return ChatFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        arguments?.let { bundle ->
            with (bundle) {
                val loggedInUser = LoggedInUser(
                    name = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_NAME, ""),
                    imageUrl = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_IMAGE_URL, ""),
                    userId = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_ID, ""),
                    documentId = getString(Constants.LoggedInUserConstants.LOGGED_IN_USER_DOCUMENT_ID, "")
                )
                viewModel.setLoggedInUser(loggedInUser)

                val chatContactUser = Contacts(
                    name = getString(Constants.ContactConstants.CONTACT_USER_NAME, ""),
                    imageUrl = getString(Constants.ContactConstants.CONTACT_USER_IMAGE_URL, ""),
                    userId = getString(Constants.ContactConstants.CONTACT_USER_ID, ""),
                    status = getString(Constants.ContactConstants.CONTACT_USER_STATUS, ContactStatus.DEFAULT.status).toContactsStatus()
                )
                viewModel.setChatContactUser(chatContactUser)
            }
        }
        val contactUserId = arguments?.getString(Constants.ContactConstants.CONTACT_USER_ID, "") ?: ""
        viewModel.getMessages(contactUserId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        binding.chatViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            Glide.with(binding.root.context)
                .load(viewModel.chatContactUser.value?.imageUrl)
                .centerCrop()
                .into(profileImage)

            messageAdapter = MessageAdapter()

            chatRv.apply {
                adapter = messageAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            }

            goBack.setOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

            sendMsgBtn.setOnClickListener {
                val messageText = (viewModel.msg.value ?: "")
                val receiverId = (viewModel.chatContactUser.value?.userId ?: "")
                if (messageText.isNotEmpty() && receiverId.isNotEmpty()) {
                    val message = Message(
                        senderId = viewModel.loggedInUser.value?.userId ?: "",
                        receiverId = receiverId,
                        message = messageText
                    )
                    enterMsg.setText("")
                    viewModel.sendMessage(message)
                }
            }
        }
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.chatMessageList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                messageAdapter.chatMessageList = list
                binding.chatRv.postDelayed({
                    binding.chatRv.scrollToPosition(0)
                }, 200)
            }
        })
    }
}