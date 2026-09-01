package com.example.iconbrowser

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Handles swapping which activity-alias (and therefore which launcher icon
 * and label) is active. Exactly one alias is enabled at a time; the rest
 * are disabled so only one icon shows up on the home screen.
 */
object IconSwitcher {

    private const val PREFS = "icon_switcher_prefs"
    private const val KEY_SELECTED = "selected_option_id"

    fun options(context: Context): List<IconOption> = listOf(
        IconOption("browser", ".AliasBrowser", context.getString(R.string.label_browser), R.drawable.ic_browser),
        IconOption("weather", ".AliasWeather", context.getString(R.string.label_weather), R.drawable.ic_weather),
        IconOption("radio", ".AliasRadio", context.getString(R.string.label_radio), R.drawable.ic_radio),
        IconOption("phone", ".AliasPhone", context.getString(R.string.label_phone), R.drawable.ic_phone),
        IconOption("calculator", ".AliasCalculator", context.getString(R.string.label_calculator), R.drawable.ic_calculator),
        IconOption("notes", ".AliasNotes", context.getString(R.string.label_notes), R.drawable.ic_notes),
        IconOption("clock", ".AliasClock", context.getString(R.string.label_clock), R.drawable.ic_clock),
        IconOption("calendar", ".AliasCalendar", context.getString(R.string.label_calendar), R.drawable.ic_calendar)
    )

    fun getSelectedId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SELECTED, "browser") ?: "browser"
    }

    fun setActiveIcon(context: Context, selectedId: String) {
        val pm = context.packageManager
        options(context).forEach { option ->
            val state = if (option.id == selectedId) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            val componentName = ComponentName(context.packageName, context.packageName + option.aliasClassName)
            pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP)
        }

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED, selectedId)
            .apply()
    }
}
