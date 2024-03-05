package com.evanemran.geminify.listeners

interface ClickListener<T> {
    fun onClicked(data: T)
}