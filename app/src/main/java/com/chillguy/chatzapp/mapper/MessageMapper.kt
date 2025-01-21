package com.chillguy.chatzapp.mapper

import com.chillguy.chatzapp.constants.Constants
import com.chillguy.chatzapp.model.dto.Message

object MessageMapper {

    fun Message.toHashMap(): HashMap<String, String>{
        val hashMap = hashMapOf(
            Constants.MessageConstants.MESSAGE to message,
            Constants.MessageConstants.SENDER to senderId,
            Constants.MessageConstants.RECEIVER to receiverId,
            Constants.MessageConstants.TIME_STAMP to timeStamp
        )
        return hashMap
    }

}