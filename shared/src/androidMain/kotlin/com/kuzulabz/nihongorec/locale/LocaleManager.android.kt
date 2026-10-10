package com.kuzulabz.nihongorec.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

actual class PlatformLocaleManager actual constructor(
    context: Any?
) : LocaleManager {
    private val appContext: Context = context as Context

    override val supportsInAppSelection: Boolean
        get() = true

//    private fun canOpenSystemLocaleSettings(): Boolean {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//            return false
//        }
//
//        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
//            putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
//        }
//
//        val packageManager = appContext.packageManager
//
//        val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            packageManager.queryIntentActivities(
//                intent,
//                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
//            )
//        } else {
//            @Suppress("DEPRECATION")
//            packageManager.queryIntentActivities(intent, 0)
//        }
//
//        if (activities.isNotEmpty()) {
//            return true
//        }
//
//        return true
//    }

    override fun openSystemSettings() {
        //    Couldn't get this to work :(
//        val packageName = appContext.packageName
//
//        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
//                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//            }
//        } else {
//            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                data = Uri.fromParts("package", packageName, null)
//            }
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//        try {
//            appContext.startActivity(intent)
//        } catch (e: Exception) {
//            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                data = Uri.fromParts("package", packageName, null)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            appContext.startActivity(fallbackIntent)
//        }
    }

    override fun applyLocale(locale: AppLocale) {
        val javaLocale = Locale.forLanguageTag(locale.code)
        Locale.setDefault(javaLocale)

        val localeList = LocaleListCompat.forLanguageTags(locale.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}