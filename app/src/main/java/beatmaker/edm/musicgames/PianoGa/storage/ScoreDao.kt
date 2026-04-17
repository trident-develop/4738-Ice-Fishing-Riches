package beatmaker.edm.musicgames.PianoGa.storage

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class ScoreDao(
    private val dbHelper: ScoreDbHelper
) {

    fun getCachedScore(): CachedScoreEntity? {
        val db = dbHelper.readableDatabase

        db.rawQuery(
            "SELECT * FROM ${ScoreDbContract.TABLE_CACHED_SCORE} WHERE ${ScoreDbContract.COLUMN_ID} = 1",
            null
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                CachedScoreEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ScoreDbContract.COLUMN_ID)),
                    score = cursor.getString(cursor.getColumnIndexOrThrow(ScoreDbContract.COLUMN_SCORE)),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(ScoreDbContract.COLUMN_CREATED_AT))
                )
            } else {
                null
            }
        }
    }

    fun saveCachedScore(link: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ScoreDbContract.COLUMN_ID, 1)
            put(ScoreDbContract.COLUMN_SCORE, link)
            put(ScoreDbContract.COLUMN_CREATED_AT, System.currentTimeMillis())
        }

        db.insertWithOnConflict(
            ScoreDbContract.TABLE_CACHED_SCORE,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun clearCachedScore() {
        val db = dbHelper.writableDatabase
        db.delete(
            ScoreDbContract.TABLE_CACHED_SCORE,
            "${ScoreDbContract.COLUMN_ID} = ?",
            arrayOf("1")
        )
    }
}