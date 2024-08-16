package com.example.whatsappclone.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Conversa(
    val idUserRem: String = "",
    val idUserDest: String = "",
    val photo: String = "",
    val name: String = "",
    val latestMessage: String = "",
    @ServerTimestamp
    val data: Date? = null,
) : Parcelable
