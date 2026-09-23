package com.example.security

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object DeviceSecurityManager {

    private val _strictSecurityEnabled = MutableStateFlow(true)
    val strictSecurityEnabled: StateFlow<Boolean> = _strictSecurityEnabled.asStateFlow()

    // Simulated overrides for QA / emulator development verification
    private val _overrideDevOptions = MutableStateFlow<Boolean?>(null)
    val overrideDevOptions: StateFlow<Boolean?> = _overrideDevOptions.asStateFlow()

    private val _overrideUsbDebugging = MutableStateFlow<Boolean?>(null)
    val overrideUsbDebugging: StateFlow<Boolean?> = _overrideUsbDebugging.asStateFlow()

    private val _integrityVerdict = MutableStateFlow(
        PlayIntegrityVerdict(
            appRecognitionVerdict = "PLAY_RECOGNIZED",
            appLicensingVerdict = "LICENSED",
            deviceRecognitionVerdict = listOf("MEETS_DEVICE_INTEGRITY", "MEETS_BASIC_INTEGRITY"),
            appAccessRisk = "NO_RISK",
            isPass = true
        )
    )
    val integrityVerdict: StateFlow<PlayIntegrityVerdict> = _integrityVerdict.asStateFlow()

    fun setStrictSecurity(enabled: Boolean) {
        _strictSecurityEnabled.value = enabled
    }

    fun setSimulatedDevOptions(enabled: Boolean?) {
        _overrideDevOptions.value = enabled
    }

    fun setSimulatedUsbDebugging(enabled: Boolean?) {
        _overrideUsbDebugging.value = enabled
    }

    fun setIntegrityVerdict(verdict: PlayIntegrityVerdict) {
        _integrityVerdict.value = verdict
    }

    /**
     * Checks if Android Developer Options are currently enabled on device.
     */
    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        _overrideDevOptions.value?.let { return it }
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if USB Debugging / ADB is currently enabled on device.
     */
    fun isUsbDebuggingEnabled(context: Context): Boolean {
        _overrideUsbDebugging.value?.let { return it }
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retrieves or creates a secure, app-scoped installation identifier.
     * Android recommends avoiding permanent hardware identifiers like IMEI/MAC
     * and using app-scoped GUIDs paired with Play Integrity verification.
     */
    fun getInstallationId(context: Context): String {
        val prefs = context.getSharedPreferences("apex_sec_store", Context.MODE_PRIVATE)
        var instId = prefs.getString("app_installation_id", null)
        if (instId == null) {
            instId = "inst_${UUID.randomUUID().toString().replace("-", "").take(16)}"
            prefs.edit().putString("app_installation_id", instId).apply()
        }
        return instId
    }

    /**
     * Constructs the UserDevice representation for the current physical/virtual device.
     */
    fun getCurrentDevice(context: Context, userId: String = ""): UserDevice {
        val instId = getInstallationId(context)
        val devOptions = isDeveloperOptionsEnabled(context)
        val usbDebug = isUsbDebuggingEnabled(context)
        val risk = if (devOptions || usbDebug) RiskLevel.MEDIUM else RiskLevel.LOW

        return UserDevice(
            deviceId = "dev_${instId.takeLast(8)}",
            userId = userId,
            installationId = instId,
            platform = "Android",
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            osVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
            appVersion = "1.0.0",
            developerOptionsEnabled = devOptions,
            usbDebuggingEnabled = usbDebug,
            riskLevel = risk,
            status = DeviceStatus.ACTIVE,
            isCurrentDevice = true
        )
    }

    /**
     * Evaluates comprehensive device integrity according to policy:
     * 1. Developer Options
     * 2. USB Debugging
     * 3. Play Integrity Verdict
     */
    fun evaluateSecurity(context: Context): SecurityDecision {
        val devOptionsOn = isDeveloperOptionsEnabled(context)
        val usbDebugOn = isUsbDebuggingEnabled(context)
        val integrity = _integrityVerdict.value

        // In strict mode, Developer Options and USB debugging are hard-blocked
        if (_strictSecurityEnabled.value) {
            if (devOptionsOn) {
                return SecurityDecision(
                    allowed = false,
                    action = SecurityDecisionAction.DISABLE_DEVELOPER_OPTIONS,
                    reason = "DEVELOPER_OPTIONS_ENABLED",
                    message = "Developer Options are currently enabled. Turn OFF to proceed."
                )
            }
            if (usbDebugOn) {
                return SecurityDecision(
                    allowed = false,
                    action = SecurityDecisionAction.DISABLE_DEVELOPER_OPTIONS,
                    reason = "USB_DEBUGGING_ENABLED",
                    message = "USB Debugging is enabled on this device."
                )
            }
        }

        if (!integrity.isPass) {
            return SecurityDecision(
                allowed = false,
                action = SecurityDecisionAction.FAILED_INTEGRITY,
                reason = "DEVICE_INTEGRITY_FAILED",
                message = "Device environment does not meet verified Play Integrity standards."
            )
        }

        return SecurityDecision(
            allowed = true,
            action = SecurityDecisionAction.ALLOW
        )
    }

    /**
     * Directs the user to Android Settings to turn off Developer Options.
     */
    fun openDeveloperSettings(context: Context) {
        val devIntent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(devIntent)
        } catch (e: Exception) {
            // Fallback to main Android system settings
            val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(settingsIntent)
            } catch (err: Exception) {
                // Ignore if restricted
            }
        }
    }
}
