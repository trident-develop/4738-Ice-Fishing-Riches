package beatmaker.edm.musicgames.PianoGa.storage

object ScoreDbContract {
    const val DATABASE_NAME = "score_storage.db"
    const val DATABASE_VERSION = 1

    const val TABLE_CACHED_SCORE = "cached_score"

    const val COLUMN_ID = "id"
    const val COLUMN_SCORE = "score"
    const val COLUMN_CREATED_AT = "created_at"

    const val CREATE_TABLE_CACHED_SCORE = """
        CREATE TABLE $TABLE_CACHED_SCORE (
            $COLUMN_ID INTEGER PRIMARY KEY NOT NULL,
            $COLUMN_SCORE TEXT NOT NULL,
            $COLUMN_CREATED_AT INTEGER NOT NULL
        )
    """
}