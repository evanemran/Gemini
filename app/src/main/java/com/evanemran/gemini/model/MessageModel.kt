package com.evanemran.gemini.model

import android.graphics.Bitmap

class MessageModel(private var mMessage: String, private var mTime: String, private var mIsReply: Boolean, private var mIsImagePrompt: Boolean, private var mBitmap: Bitmap?) {
    var message: String = mMessage
    var time: String = mTime
    var isReply: Boolean = mIsReply
    var isImagePrompt: Boolean = mIsImagePrompt
    var image: Bitmap? = mBitmap
}