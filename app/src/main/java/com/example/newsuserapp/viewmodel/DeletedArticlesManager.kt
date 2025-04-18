package com.example.newsuserapp.viewmodel

import android.content.Context

object DeletedArticlesManager {
    private const val PREF_NAME = "deleted_articles"
    private const val KEY_DELETED_URLS = "deleted_urls"

    fun getDeletedUrls(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DELETED_URLS, emptySet()) ?: emptySet()
    }

    fun addDeletedUrl(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val deletedUrls = getDeletedUrls(context).toMutableSet()
        deletedUrls.add(url)

        prefs.edit().putStringSet(KEY_DELETED_URLS, deletedUrls).apply()
    }
}

