package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.content.SharedPreferences

internal object PinRateLimiter {
    private const val KEY_PIN_FAILED_COUNT = "pinFailedCount"
    private const val KEY_PIN_LOCKOUT_UNTIL = "pinLockoutUntilEpochMs"

    private const val LOCKOUT_THRESHOLD_SOFT = 5
    private const val LOCKOUT_THRESHOLD_HARD = 10
    private const val LOCKOUT_DURATION_SOFT_MS = 60_000L
    private const val LOCKOUT_DURATION_HARD_MS = 5 * 60_000L

    fun isLocked(sp: SharedPreferences): Boolean = remainingMs(sp) > 0L

    fun remainingMs(sp: SharedPreferences): Long {
        val remaining = currentLockoutUntil(sp) - System.currentTimeMillis()
        return if (remaining > 0L) remaining else 0L
    }

    fun recordFailure(sp: SharedPreferences) {
        val next = currentFailedCount(sp) + 1
        val duration = computeLockoutDurationMs(next)
        val editor = sp.edit().putInt(KEY_PIN_FAILED_COUNT, next)
        if (duration > 0L) {
            editor.putLong(KEY_PIN_LOCKOUT_UNTIL, System.currentTimeMillis() + duration)
        }
        editor.apply()
    }

    fun clear(sp: SharedPreferences) {
        sp.edit()
            .remove(KEY_PIN_FAILED_COUNT)
            .remove(KEY_PIN_LOCKOUT_UNTIL)
            .apply()
    }

    private fun currentFailedCount(sp: SharedPreferences): Int =
        sp.getInt(KEY_PIN_FAILED_COUNT, 0)

    private fun currentLockoutUntil(sp: SharedPreferences): Long =
        sp.getLong(KEY_PIN_LOCKOUT_UNTIL, 0L)

    private fun computeLockoutDurationMs(failedCount: Int): Long = when {
        failedCount < LOCKOUT_THRESHOLD_SOFT -> 0L
        failedCount < LOCKOUT_THRESHOLD_HARD -> LOCKOUT_DURATION_SOFT_MS
        else -> LOCKOUT_DURATION_HARD_MS
    }
}
