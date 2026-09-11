package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SecurityPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("7Diary", appName)
  }

  @Test
  fun `verify pin security encryption`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = SecurityPreferences(context)
    prefs.setPin("1234")
    assertTrue(prefs.isPinSet())
    assertTrue(prefs.verifyPin("1234"))
    assertFalse(prefs.verifyPin("0000"))
  }

  @Test
  fun `verify 7-day cycle generation and day numbering`() {
    val cycle = com.example.data.model.SevenDayCycleHelper.getCycleForWeekOffset(0, emptyList())
    assertEquals(7, cycle.days.size)
    assertEquals(1, cycle.days[0].dayNumber)
    assertEquals(7, cycle.days[6].dayNumber)
    assertEquals(0, cycle.recordedDaysCount)
    assertFalse(cycle.isComplete)

    val dayNum = com.example.data.model.SevenDayCycleHelper.getDayNumberInCycle(System.currentTimeMillis())
    assertTrue(dayNum in 1..7)
  }

  @Test
  fun `verify 7-day cycle reflection persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = SecurityPreferences(context)
    val cycleKey = "2026-W37"
    prefs.setCycleReflection(cycleKey, "A great productive week!")
    assertEquals("A great productive week!", prefs.getCycleReflection(cycleKey))
  }
}
