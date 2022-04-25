package com.app.merorecipe.global

import android.content.Context
import android.widget.Toast

class GlobalClass {
    companion object {
        fun globalToast(context: Context? = null, strMessage: String) {
            Toast.makeText(context, "${strMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}