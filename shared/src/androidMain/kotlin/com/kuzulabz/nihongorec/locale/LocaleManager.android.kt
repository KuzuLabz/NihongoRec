package com.kuzulabz.nihongorec.locale

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

actual class PlatformLocaleManager actual constructor(
    context: Any?
) : LocaleManager {
    private val appContext: Context = context as Context

    override val supportsInAppSelection: Boolean
        get() = false

    override fun openSystemSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                data = Uri.fromParts("package", appContext.packageName, null)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", appContext.packageName, null)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }

    override fun applyLocale(locale: AppLocale) {
        // Not needed
    }
}