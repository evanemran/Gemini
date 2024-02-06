package com.evanemran.gemini.listeners

interface ClickListener<T> {
    fun onClicked(data: T)
}