package com.kuzulabz.nihongorec.datastore

import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual fun getDataStorePath(): Path {
    val file = File(System.getProperty("user.home"), ".kanjiapp/app_settings.preferences_pb")
    file.parentFile?.mkdirs()
    return file.absolutePath.toPath()
}