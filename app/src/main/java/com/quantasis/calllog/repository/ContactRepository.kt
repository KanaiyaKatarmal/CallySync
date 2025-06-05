package com.quantasis.calllog.repository

import android.content.Context
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.quantasis.calllog.database.AppDatabase
import com.quantasis.calllog.database.ContactDao
import com.quantasis.calllog.database.ContactEntity
import com.quantasis.calllog.database.ContactNumberEntity
import com.quantasis.calllog.manager.CallLogSyncManager
import com.quantasis.calllog.util.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale

class ContactRepository(
    private val context: Context,
    private val appDb: AppDatabase
) {

    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    private val syncMutex = Mutex()
    var lastSyncedTime: Long
        get() = prefs.getLong("last_synced_time", 0L)
        set(value) = prefs.edit().putLong("last_synced_time", value).apply()

    suspend fun syncContactsWithDevice() {
        syncMutex.withLock {
            withContext(Dispatchers.IO) {
                val contentResolver = context.contentResolver
                val lastSynced = lastSyncedTime
                val uri = ContactsContract.Contacts.CONTENT_URI
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val simCountry = telephonyManager.simCountryIso.uppercase(Locale.getDefault())
                val contactDao = appDb.contactDao();

                val projection = arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
                    ContactsContract.Contacts.PHOTO_URI
                )

                val selection = "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} > ?"
                val selectionArgs = arrayOf(lastSynced.toString())

                val cursor =
                    contentResolver.query(uri, projection, selection, selectionArgs, null) ?: return@withContext

                val contacts = mutableListOf<ContactEntity>()
                val numbers = mutableListOf<ContactNumberEntity>()
                var maxUpdated = lastSynced

                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val updated = cursor.getLong(2)
                    val photoUri = cursor.getString(3)
                    val contact = ContactEntity(id, name, updated, photoUri)
                    contacts.add(contact)
                    if (updated > maxUpdated) maxUpdated = updated

                    // Delete old numbers
                    contactDao.deleteNumbersForContact(id)
                    val phoneSet = mutableSetOf<String>()
                    // Fetch all numbers
                    val phonesCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id), null
                    )

                    phonesCursor?.use {
                        while (it.moveToNext()) {
                            val number = it.getString(0)

                            val result =
                                PhoneNumberUtils.extractPhoneNumberParts(number, simCountry)

                            // Only insert if not already seen
                            if (result.nationalNumber.isNotEmpty() && result.nationalNumber !in phoneSet) {
                                phoneSet.add(result.nationalNumber)
                                numbers.add(
                                    ContactNumberEntity(
                                        contactId = id,
                                        phone = result.nationalNumber,
                                        rawNumber = number,
                                        countryCode = result.countryCode
                                    )
                                )
                            }
                        }
                    }
                }
                cursor.close()

                if (contacts.isNotEmpty()) {
                    contactDao.insertContacts(contacts)
                    contactDao.insertNumbers(numbers)
                    lastSyncedTime = maxUpdated
                }
            }
        }
    }
}