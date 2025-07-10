package ru.moviechecker.database

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE sites SET `mnemonic` = 'lostfilm' WHERE `address` like '%lostfilm%'")
        db.execSQL("UPDATE sites SET `address` = 'https://www.lostfilm.tv' WHERE `mnemonic` = 'lostfilm'")
        db.execSQL("UPDATE sites SET `mirror` = 'https://www.lostfilm.download' WHERE `mnemonic` = 'lostfilm'")

        db.execSQL("UPDATE sites SET `mnemonic` = 'amedia' WHERE `address` like '%amedia%'")
        db.execSQL("UPDATE sites SET `address` = 'https://amedia.online' WHERE `mnemonic` = 'amedia'")
        db.execSQL("UPDATE sites SET `mirror` = 'https://a1.amedia.so' WHERE `mnemonic` = 'amedia'")
    }
}
