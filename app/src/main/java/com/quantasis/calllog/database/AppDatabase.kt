package com.quantasis.calllog.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import kotlin.jvm.Volatile

@Database(
    entities = [CallLogEntity::class,ContactEntity::class, ContactNumberEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao

    abstract fun contactDao(): ContactDao

    companion object {
        private const val DB_NAME = "Instagram.db"
        private const val DB_PASSWORD = "myStrongPassword" // Ideally from secure source

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: create(context).also { instance = it }
            }
        }

        private fun create(context: Context): AppDatabase {
            // Derive a support factory using your password
            val passphrase: ByteArray = SQLiteDatabase.getBytes(DB_PASSWORD.toCharArray())
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .build()
        }
    }
}