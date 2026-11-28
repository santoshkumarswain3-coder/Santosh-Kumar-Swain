package com.example

import com.example.data.BuiltInQuestionBank
import com.example.data.model.GradeCatalog
import com.example.data.model.LevelsCatalog
import com.example.data.model.QuestionCategory
import com.example.data.model.QuizQuestion
import com.example.data.repository.QuestionSelector
import com.example.notifications.QuizNotificationScheduler
import com.example.notifications.QuizReminderKind
import com.example.util.AppStrings
import com.example.util.LanguageCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone
import kotlin.random.Random

class QuizFeaturesUnitTest {

    private val utc: TimeZone = TimeZone.getTimeZone("UTC")

    private fun calAt(year: Int, monthZeroBased: Int, day: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance(utc).apply {
            set(year, monthZeroBased, day, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    @Test
    fun dailyTrigger_schedulesSameDayWhenBefore1930_andNextDayWhenAfter() {
        val before = calAt(2026, Calendar.NOVEMBER, 27, 19, 0)
        val triggerBefore = QuizNotificationScheduler.computeNextTriggerMillis(
            QuizReminderKind.DAILY,
            before,
            utc
        )
        assertEquals(calAt(2026, Calendar.NOVEMBER, 27, 19, 30), triggerBefore)

        val atOrAfter = calAt(2026, Calendar.NOVEMBER, 27, 19, 30)
        val triggerAfter = QuizNotificationScheduler.computeNextTriggerMillis(
            QuizReminderKind.DAILY,
            atOrAfter,
            utc
        )
        assertEquals(calAt(2026, Calendar.NOVEMBER, 28, 19, 30), triggerAfter)
    }

    @Test
    fun weeklyTrigger_schedulesSundayAt1930() {
        // Friday Nov 27, 2026 -> Sunday Nov 29, 2026 at 19:30
        val friday = calAt(2026, Calendar.NOVEMBER, 27, 10, 0)
        assertEquals(
            calAt(2026, Calendar.NOVEMBER, 29, 19, 30),
            QuizNotificationScheduler.computeNextTriggerMillis(QuizReminderKind.WEEKLY, friday, utc)
        )

        // Sunday Nov 29, 2026 19:35 -> next Sunday Dec 6, 2026 at 19:30
        val sundayEvening = calAt(2026, Calendar.NOVEMBER, 29, 19, 35)
        assertEquals(
            calAt(2026, Calendar.DECEMBER, 6, 19, 30),
            QuizNotificationScheduler.computeNextTriggerMillis(QuizReminderKind.WEEKLY, sundayEvening, utc)
        )
    }

    @Test
    fun monthlyTrigger_schedulesLastDayOfMonthAt1930_includingFebAndMonthRollover() {
        // Mid-November 2026 -> Nov 30, 2026 19:30
        val midNov = calAt(2026, Calendar.NOVEMBER, 15, 12, 0)
        assertEquals(
            calAt(2026, Calendar.NOVEMBER, 30, 19, 30),
            QuizNotificationScheduler.computeNextTriggerMillis(QuizReminderKind.MONTHLY, midNov, utc)
        )

        // Jan 31, 2028 20:00 -> Feb 29, 2028 (leap year) 19:30
        val janEndLeap = calAt(2028, Calendar.JANUARY, 31, 20, 0)
        assertEquals(
            calAt(2028, Calendar.FEBRUARY, 29, 19, 30),
            QuizNotificationScheduler.computeNextTriggerMillis(QuizReminderKind.MONTHLY, janEndLeap, utc)
        )

        // Jan 31, 2026 20:00 -> Feb 28, 2026 (non-leap year) 19:30
        val janEndNonLeap = calAt(2026, Calendar.JANUARY, 31, 20, 0)
        assertEquals(
            calAt(2026, Calendar.FEBRUARY, 28, 19, 30),
            QuizNotificationScheduler.computeNextTriggerMillis(QuizReminderKind.MONTHLY, janEndNonLeap, utc)
        )
    }

    @Test
    fun masterCertificateThreshold_isTenMillionCoins() {
        assertEquals(10_000_000L, LevelsCatalog.MASTER_COINS)
        assertEquals("EXCLUSIVE", LevelsCatalog.levelForCoins(9_999_999L).title)
        assertEquals("Being Master", LevelsCatalog.levelForCoins(10_000_000L).title)
    }

    @Test
    fun gradeCatalog_parsesAndNormalizesGradesCorrectly() {
        assertEquals(1, GradeCatalog.parseGradeNumber("Grade 1"))
        assertEquals(12, GradeCatalog.parseGradeNumber("Class 12 Science"))
        assertEquals(5, GradeCatalog.parseGradeNumber(null))
        assertEquals(1, GradeCatalog.parseGradeNumber("0"))
        assertEquals(12, GradeCatalog.parseGradeNumber("99"))
        assertEquals("Grade 8", GradeCatalog.normalizeGrade("8"))
        assertEquals(12, GradeCatalog.grades.size)
    }

    @Test
    fun questionSelector_prioritizesGradeAndAvoidsConsecutiveRepeats() {
        val pool = (1..30).map { i ->
            QuizQuestion(
                id = "q_$i",
                category = QuestionCategory.MATHEMATICS,
                questionText = "Question $i",
                options = listOf("A", "B", "C", "D"),
                correctIndex = 0,
                explanation = "Explanation $i",
                minGrade = if (i <= 20) 1 else 10,
                maxGrade = if (i <= 20) 4 else 12
            )
        }

        // First draw for Grade 2 (10 questions): must come strictly from grades 1..4
        val firstTest = QuestionSelector.selectQuestions(
            pool = pool,
            userGrade = 2,
            requestedCount = 10,
            random = Random(42)
        )
        assertEquals(10, firstTest.size)
        assertTrue(firstTest.all { it.matchesGrade(2) })

        val seenAfterFirst = firstTest.map { it.id }.toSet()
        val lastAfterFirst = seenAfterFirst

        // Second draw for Grade 2 (10 questions): must get the remaining 10 grade-matched unseen questions (zero overlap!)
        val secondTest = QuestionSelector.selectQuestions(
            pool = pool,
            userGrade = 2,
            requestedCount = 10,
            seenIds = seenAfterFirst,
            lastTestIds = lastAfterFirst,
            random = Random(43)
        )
        assertEquals(10, secondTest.size)
        assertTrue(secondTest.all { it.matchesGrade(2) })
        assertTrue(secondTest.none { it.id in seenAfterFirst })

        // Third draw when only 10 unseen non-matching questions remain and all 20 grade-matched are seen:
        // Unseen pool (q_21..q_30) comes first, and still excludes lastTestIds!
        val seenAfterSecond = seenAfterFirst + secondTest.map { it.id }
        val lastAfterSecond = secondTest.map { it.id }.toSet()
        val thirdTest = QuestionSelector.selectQuestions(
            pool = pool,
            userGrade = 2,
            requestedCount = 10,
            seenIds = seenAfterSecond,
            lastTestIds = lastAfterSecond,
            random = Random(44)
        )
        assertEquals(10, thirdTest.size)
        assertTrue(thirdTest.none { it.id in seenAfterSecond })
        assertTrue(thirdTest.none { it.id in lastAfterSecond })

        // Fourth draw after wraparound (seen = lastTestIds, so Priority 3 returns grade-matched NOT in lastTestIds)
        val fourthTest = QuestionSelector.selectQuestions(
            pool = pool,
            userGrade = 2,
            requestedCount = 10,
            seenIds = lastAfterSecond,
            lastTestIds = lastAfterSecond,
            random = Random(45)
        )
        assertEquals(10, fourthTest.size)
        assertTrue(fourthTest.none { it.id in lastAfterSecond })
    }

    @Test
    fun builtInQuestionBank_isValidAcrossAllSubjectsAndGrades() {
        val bank = BuiltInQuestionBank.questions
        assertTrue("Expected at least 100 built-in questions, got ${bank.size}", bank.size >= 100)

        // All IDs must be unique
        val ids = bank.map { it.id }
        assertEquals(ids.size, ids.toSet().size)

        // Every question must have 4 options, valid answer index, and valid grade bounds
        for (q in bank) {
            assertEquals("Question ${q.id} should have 4 options", 4, q.options.size)
            assertTrue("Question ${q.id} has invalid correctIndex", q.correctIndex in 0..3)
            assertTrue("Question ${q.id} has blank text", q.questionText.isNotBlank())
            assertTrue("Question ${q.id} has invalid grades", q.minGrade in 1..12 && q.maxGrade in q.minGrade..12)
        }

        // All 11 subjects must have questions
        for (category in QuestionCategory.entries) {
            val count = bank.count { it.category == category }
            assertTrue("Subject ${category.displayName} should have >= 8 questions, found $count", count >= 8)
        }

        // Every grade 1..12 must have >= 30 directly matching questions
        for (grade in 1..12) {
            val gradeCount = bank.count { it.matchesGrade(grade) }
            assertTrue("Grade $grade should have >= 30 questions, found $gradeCount", gradeCount >= 30)
        }
    }

    @Test
    fun localization_supportsMultipleLanguagesWithDistinctStrings() {
        val english = AppStrings.forLanguage("English")
        val hindi = AppStrings.forLanguage("Hindi")
        val odia = AppStrings.forLanguage("Odia")
        val spanish = AppStrings.forLanguage("Spanish")

        assertEquals("English", LanguageCatalog.normalize(null))
        assertEquals("Hindi", LanguageCatalog.normalize("हिन्दी"))
        assertEquals("Odia", LanguageCatalog.normalize("odia"))

        assertTrue(LanguageCatalog.languages.size >= 9)
        assertNotEquals(english.navHome, hindi.navHome)
        assertNotEquals(english.navHome, odia.navHome)
        assertNotEquals(english.navHome, spanish.navHome)
        assertTrue(hindi.geminiSignInButton.isNotBlank())
        assertTrue(odia.selectLanguageLabel.isNotBlank())
    }
}
