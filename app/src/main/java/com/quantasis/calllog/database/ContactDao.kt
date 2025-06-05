package com.quantasis.calllog.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ContactDao {

    @Transaction
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<ContactWithNumbers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Insert
    suspend fun insertNumbers(numbers: List<ContactNumberEntity>)

    @Query("DELETE FROM contacts WHERE id NOT IN (:ids)")
    suspend fun deleteMissingContacts(ids: List<String>)

    @Query("DELETE FROM contact_numbers WHERE contactId = :contactId")
    suspend fun deleteNumbersForContact(contactId: String)


}