package com.example.data.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"

private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val APPCHECK_META_PREFS = "brightquiz_appcheck_meta"
private const val KEY_INTENT_TOKEN_ACTIVE = "has_intent_debug_token"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

object AppCheckInitState {
    @Volatile
    var providerSummary: String = "uninitialized"
        internal set
}

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        val metaPrefs = context.getSharedPreferences(APPCHECK_META_PREFS, Context.MODE_PRIVATE)
        val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)

        if (BuildConfig.DEBUG && !debugToken.isNullOrEmpty()) {
            val persistenceKey = FirebaseApp.getInstance().persistenceKey
            val prefs = context.getSharedPreferences(
                "$DEBUG_STORE_PREFIX$persistenceKey",
                Context.MODE_PRIVATE
            )
            prefs.edit()
                .putString(DEBUG_SECRET_KEY, debugToken)
                .commit()
            metaPrefs.edit()
                .putBoolean(KEY_INTENT_TOKEN_ACTIVE, true)
                .commit()
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            AppCheckInitState.providerSummary = "DebugIntent"
            Log.d(TAG, "App Check debug secret configured from Intent; DebugAppCheckProviderFactory installed.")
            return
        }

        if (BuildConfig.DEBUG && metaPrefs.getBoolean(KEY_INTENT_TOKEN_ACTIVE, false)) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            AppCheckInitState.providerSummary = "DebugCachedIntent"
            Log.d(TAG, "Using previously configured Intent debug token.")
            return
        }

        val resId = context.resources.getIdentifier(
            RECAPTCHA_SITE_KEY_RES,
            "string",
            context.packageName
        )
        val rawSiteKey = if (resId != 0) {
            context.getString(resId)
        } else {
            runCatching { context.getString(R.string.recaptcha_site_key) }.getOrDefault("")
        }
        val siteKey = rawSiteKey.trim()
        if (siteKey.isNotBlank()) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                RecaptchaAppCheckProviderFactory.getInstance(siteKey)
            )
            AppCheckInitState.providerSummary = "RecaptchaEnterprise"
            Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
        } else if (BuildConfig.DEBUG) {
            Log.w(TAG, "No debug token or recaptcha_site_key found; falling back to DebugAppCheckProviderFactory.")
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            AppCheckInitState.providerSummary = "DebugFallbackNoSiteKey"
        } else {
            AppCheckInitState.providerSummary = "MissingSiteKey"
            Log.e(TAG, "recaptcha_site_key string resource not found or blank in package.")
        }
    } catch (e: Exception) {
        AppCheckInitState.providerSummary = "InitError:${e.javaClass.simpleName}"
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}
