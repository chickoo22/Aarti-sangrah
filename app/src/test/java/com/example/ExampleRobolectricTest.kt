package com.example

import com.example.data.AartiEntity
import com.example.data.AartiSeedData
import com.example.notifications.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalNotificationAndPrayerTest {

    @Test
    fun testNotificationTimeFormatting() {
        val morning = NotificationHelper.formatTime(6, 30)
        assertEquals("06:30 AM", morning)

        val evening = NotificationHelper.formatTime(19, 0)
        assertEquals("07:00 PM", evening)

        val midnight = NotificationHelper.formatTime(0, 0)
        assertEquals("12:00 AM", midnight)

        val noon = NotificationHelper.formatTime(12, 15)
        assertEquals("12:15 PM", noon)
    }

    @Test
    fun testNotificationConstants() {
        assertEquals("mantramaya_daily_prayers", NotificationHelper.CHANNEL_ID_PRAYER)
        assertEquals(101, NotificationHelper.ID_MORNING_REMINDER)
        assertEquals(102, NotificationHelper.ID_EVENING_REMINDER)
        assertEquals(103, NotificationHelper.ID_SPECIAL_REMINDER)
        assertEquals(100, NotificationHelper.ID_INSTANT_TEST)
    }

    @Test
    fun testSeedPrayersAvailable() {
        val prayers = AartiSeedData.getSeedAartis()
        assertTrue("Seed prayers should not be empty", prayers.isNotEmpty())

        val durga = prayers.find { it.titleMarathi.contains("दुर्गे") }
        assertNotNull("Durge Durghat Bhari should be present", durga)

        val hanuman = prayers.find { it.titleEnglish.contains("Hanuman Chalisa") }
        assertNotNull("Hanuman Chalisa should be present", hanuman)

        val vitthal = prayers.find { it.titleMarathi.contains("विठ्ठले") }
        assertNotNull("Yeyi Ho Vitthale should be present", vitthal)

        val ghalin = prayers.find { it.titleMarathi.contains("घालीन") }
        assertNotNull("Ghalin Lotangan should be present", ghalin)
    }

    @Test
    fun testDeityCoverage() {
        val prayers = AartiSeedData.getSeedAartis()
        val deities = prayers.map { it.deity }.distinct()
        assertTrue("Should contain Ganesha", deities.contains("Ganesha"))
        assertTrue("Should contain Shiva", deities.contains("Shiva"))
        assertTrue("Should contain Vishnu", deities.contains("Vishnu"))
        assertTrue("Should contain Hanuman", deities.contains("Hanuman"))
        assertTrue("Should contain Durga", deities.contains("Durga"))
        assertTrue("Should contain Shani", deities.contains("Shani"))
        assertTrue("Should contain Saraswati", deities.contains("Saraswati"))
    }

    @Test
    fun testMultilingualContentNonEmpty() {
        val prayers = AartiSeedData.getSeedAartis()
        for (p in prayers) {
            assertTrue("English title should not be blank for ${p.deity}", p.titleEnglish.isNotBlank())
            assertTrue("Hindi title should not be blank for ${p.deity}", p.titleHindi.isNotBlank())
            assertTrue("Marathi title should not be blank for ${p.deity}", p.titleMarathi.isNotBlank())

            assertTrue("English lyrics should not be blank for ${p.titleEnglish}", p.lyricsEnglish.isNotBlank())
            assertTrue("Hindi lyrics should not be blank for ${p.titleEnglish}", p.lyricsHindi.isNotBlank())
            assertTrue("Marathi lyrics should not be blank for ${p.titleEnglish}", p.lyricsMarathi.isNotBlank())
        }
    }

    @Test
    fun testSearchFilteringLogic() {
        val prayers = AartiSeedData.getSeedAartis()

        // English search
        val chalisaResults = prayers.filter {
            it.titleEnglish.contains("Chalisa", ignoreCase = true)
        }
        assertTrue("Should find chalisas in English", chalisaResults.size >= 2)

        // Marathi keyword search
        val marathiDurgaResults = prayers.filter {
            it.titleMarathi.contains("दुर्गे") || it.lyricsMarathi.contains("दुर्गे")
        }
        assertTrue("Should find Durge in Marathi", marathiDurgaResults.isNotEmpty())

        // Hindi keyword search
        val hindiAartiResults = prayers.filter {
            it.titleHindi.contains("आरती") || it.lyricsHindi.contains("आरती")
        }
        assertTrue("Should find aartis in Hindi", hindiAartiResults.isNotEmpty())
    }

    @Test
    fun testFavoriteToggle() {
        val prayer = AartiEntity(
            id = 1,
            deity = "Ganesha",
            titleEnglish = "Ganesh Aarti",
            titleHindi = "गणेश आरती",
            titleMarathi = "गणेश आरती",
            lyricsEnglish = "Sukhkarta Dukhharta",
            lyricsHindi = "सुखकर्ता दुखहर्ता",
            lyricsMarathi = "सुखकर्ता दुखहर्ता",
            isFavorite = false,
            category = "Aarti"
        )
        assertFalse(prayer.isFavorite)

        val updated = prayer.copy(isFavorite = !prayer.isFavorite)
        assertTrue(updated.isFavorite)
        assertEquals(prayer.id, updated.id)
    }

    @Test
    fun testBhagwaBrandColor() {
        assertEquals(1.0f, com.example.ui.theme.BhagwaPrimary.red, 0.001f)
        assertEquals(0.6f, com.example.ui.theme.BhagwaPrimary.green, 0.001f)
        assertEquals(0.2f, com.example.ui.theme.BhagwaPrimary.blue, 0.001f)
    }

    @Test
    fun testLanguageSelectionInvariant() {
        val supportedLangs = listOf("mr", "hi", "en")
        assertTrue(supportedLangs.contains("mr"))
        assertTrue(supportedLangs.contains("hi"))
        assertTrue(supportedLangs.contains("en"))
    }

    @Test
    fun testBhajansPresentForAllDeities() {
        val prayers = AartiSeedData.getSeedAartis()
        val bhajans = prayers.filter { it.category == "Bhajan" }
        assertTrue("Should have multiple bhajans", bhajans.size >= 10)

        val deities = listOf("Ganesha", "Shiva", "Vishnu", "Hanuman", "Durga", "Shani", "Saraswati")
        for (deity in deities) {
            val deityBhajans = bhajans.filter { it.deity.equals(deity, ignoreCase = true) }
            assertTrue("Deity $deity must have at least one bhajan", deityBhajans.isNotEmpty())
        }
    }

    @Test
    fun testBhajanLyricsComplete() {
        val bhajans = AartiSeedData.getSeedAartis().filter { it.category == "Bhajan" }
        for (b in bhajans) {
            assertTrue("Marathi lyrics must be present for ${b.titleEnglish}", b.lyricsMarathi.isNotBlank())
            assertTrue("Hindi lyrics must be present for ${b.titleEnglish}", b.lyricsHindi.isNotBlank())
            assertTrue("English lyrics must be present for ${b.titleEnglish}", b.lyricsEnglish.isNotBlank())
        }
    }
}
