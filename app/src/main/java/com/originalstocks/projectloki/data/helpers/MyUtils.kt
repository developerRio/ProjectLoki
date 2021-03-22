package com.originalstocks.projectloki.data.helpers

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

enum class Status {
    INIT,
    SAVING,
    DATA_SAVED,
    ERROR,
    DELETING,
    DELETED,
    SUCCESS
}

const val peekHeight = 350
const val REQUEST_PERMISSION_LOCATION = 103

fun getApiKey(): String? {
    return SharedPref.read(SharedPref.AUTH_KEY, "")
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showSnackBar(rootView: View, message: String) {
    val snackBar: Snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
    snackBar.setAction("Ok") {
        snackBar.dismiss()
    }
    snackBar.show()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}