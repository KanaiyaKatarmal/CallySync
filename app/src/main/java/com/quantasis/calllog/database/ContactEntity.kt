package com.quantasis.calllog.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val lastUpdated: Long,
    val photoUri: String? // URI string to contact photo
)

@Entity(
    tableName = "contact_numbers",
    foreignKeys = [ForeignKey(
        entity = ContactEntity::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("contactId")]
)
data class ContactNumberEntity(
    @PrimaryKey(autoGenerate = true) val numberId: Long = 0,
    val contactId: String,
    val phone: String,
    val rawNumber: String,
    val countryCode: String?
)

data class ContactWithNumbers(
    @Embedded val contact: ContactEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val numbers: List<ContactNumberEntity>
)