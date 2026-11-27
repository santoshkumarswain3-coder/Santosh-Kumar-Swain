package com.example

import com.example.data.model.LevelsCatalog
import com.example.notifications.QuizNotificationScheduler
import com.example.notifications.QuizReminderKind
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

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
}
