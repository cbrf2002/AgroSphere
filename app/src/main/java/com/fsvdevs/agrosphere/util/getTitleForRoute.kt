package com.fsvdevs.agrosphere.util

import com.fsvdevs.agrosphere.routes.Routes

fun getTitleForRoute(route: String?): String {
    return when (route) {
        Routes.MONITOR_SCREEN -> "Monitor"
        Routes.NOTIFICATIONS_SCREEN -> "Notifications"
        Routes.PREFERENCES_SCREEN -> "Preferences"
        else -> ""
    }
}