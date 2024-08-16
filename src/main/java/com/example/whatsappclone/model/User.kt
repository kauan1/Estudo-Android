package com.example.whatsappclone.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var photo: String = ""
) : Parcelable