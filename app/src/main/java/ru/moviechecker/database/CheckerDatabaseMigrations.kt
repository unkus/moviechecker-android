package ru.moviechecker.database

import androidx.room3.RenameColumn
import androidx.room3.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL

@RenameColumn.Entries(
    RenameColumn(
        tableName = "movies",
        fromColumnName = "poster_link",
        toColumnName = "poster"
    ),
    RenameColumn(
        tableName = "seasons",
        fromColumnName = "poster_link",
        toColumnName = "poster"
    )
)
class Ver1To2AutoMigration : AutoMigrationSpec

class Ver8To9AutoMigration : AutoMigrationSpec {
    override suspend fun onPostMigrate(connection: SQLiteConnection) {
        super.onPostMigrate(connection)

        connection.execSQL("UPDATE sites SET `mnemonic` = 'lostfilm' WHERE `address` like '%lostfilm%'")
        connection.execSQL("UPDATE sites SET `address` = 'https://www.lostfilm.tv' WHERE `mnemonic` = 'lostfilm'")
        connection.execSQL("UPDATE sites SET `mirror` = 'https://www.lostfilm.download' WHERE `mnemonic` = 'lostfilm'")

        connection.execSQL("UPDATE sites SET `mnemonic` = 'amedia' WHERE `address` like '%amedia%'")
        connection.execSQL("UPDATE sites SET `address` = 'https://amedia.online' WHERE `mnemonic` = 'amedia'")
        connection.execSQL("UPDATE sites SET `mirror` = 'https://a1.amedia.so' WHERE `mnemonic` = 'amedia'")
    }
}
