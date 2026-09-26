package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AartiDao
import com.example.data.AartiEntity
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AartiDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.aartiDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Divine Aarti", appName)
    }

    @Test
    fun `test aarti dao insert and query`() = runBlocking {
        val aarti = AartiEntity(
            deity = "Ganesha",
            titleEnglish = "Test Aarti",
            titleHindi = "टेस्ट आरती",
            titleMarathi = "टेस्ट आरती",
            lyricsEnglish = "Test Lyrics",
            lyricsHindi = "टेस्ट लिरिक्स",
            lyricsMarathi = "टेस्ट लिरिक्स",
            audioUrl = "https://example.com/audio.mp3",
            isFavorite = false,
            category = "Ganesha"
        )
        dao.insertAarti(aarti)
        val list = dao.getAllAartis().first()
        assertEquals(1, list.size)
        assertEquals("Test Aarti", list[0].titleEnglish)
    }

    @Test
    fun `test favorite update toggle`() = runBlocking {
        val aarti = AartiEntity(
            deity = "Shiva",
            titleEnglish = "Shiva Aarti",
            titleHindi = "शिव आरती",
            titleMarathi = "शिव आरती",
            lyricsEnglish = "Om Namah Shivaya",
            lyricsHindi = "ॐ नमः शिवाय",
            lyricsMarathi = "ॐ नमः शिवाय",
            audioUrl = "https://example.com/shiva.mp3",
            isFavorite = false,
            category = "Shiva"
        )
        dao.insertAarti(aarti)
        val saved = dao.getAllAartis().first()[0]
        
        dao.updateFavorite(saved.id, true)
        val favs = dao.getFavoriteAartis().first()
        assertEquals(1, favs.size)
        assertTrue(favs[0].isFavorite)
    }
}
