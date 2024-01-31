package com.evanemran.gemini.model

class MessageModel(private var mMessage: String, private var mTime: String, private var mIsReply: Boolean) {
    var message: String = mMessage
    var time: String = mTime
    var isReply: Boolean = mIsReply
}