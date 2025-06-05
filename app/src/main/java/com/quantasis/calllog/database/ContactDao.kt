package com.quantasis.calllog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.quantasis.calllog.datamodel.ContactCallInfo

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

    @Query(
        """
        SELECT 
            c.id AS contactId,
            c.name AS contactName,
            c.photoUri AS photoUri,
            n.phone AS phone,
            COUNT(cl.id) AS callCount
        FROM contacts c
        INNER JOIN contact_numbers n ON c.id = n.contactId
        LEFT JOIN calllog cl ON cl.number = n.phone
        GROUP BY n.phone
        ORDER BY callCount DESC
        """
    )
    fun getContactCallInfoList(): LiveData<List<ContactCallInfo>>

}