package com.kuzulabz.nihongorec.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import okio.Path

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()
private var dataStoreInstance: DataStore<Preferences>? = null

@OptIn(InternalCoroutinesApi::class)
fun createDataStore(producePath: () -> Path): DataStore<Preferences> {
    return synchronized(lock) {
        dataStoreInstance ?: PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath() }
        ).also { dataStoreInstance = it }
    }
}