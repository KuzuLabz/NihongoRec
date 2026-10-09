package com.kuzulabz.nihongorec.datastore

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.context.GlobalContext

actual fun getDataStorePath(): Path {
    // Retrieves the Context passed into androidContext(this) during Koin startup
    val context: Context = GlobalContext.get().get()
    return context.filesDir
        .resolve("datastore/app_settings.preferences_pb")
        .absolutePath
        .toPath()
}