package com.dicoding.githubexp1.database

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class FavoriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val ID = "id"
            const val AVATAR = "avatar_url"
            const val HTML = "html_url"
            const val LOGIN = "login"
        }
    }
}