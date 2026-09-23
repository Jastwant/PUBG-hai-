package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object ReminderVibrationManager {

    /**
     * Triggers a distinct haptic pulse pattern for the 2-minute match reminder.
     * Pattern: [delay, pulse, rest, pulse, rest, long_pulse]
     */
    fun triggerMatchStartingVibration(context: Context) {
        try {
            val pattern = longArrayOf(0, 350, 150, 350, 150, 600)
            val amplitudes = intArrayOf(0, 200, 0, 255, 0, 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.let {
                    val effect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
                    it.vibrate(effect)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.let {
                    val effect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
                    it.vibrate(effect)
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.w("ReminderVibration", "Could not trigger device vibration: ${e.message}")
        }
    }
}
