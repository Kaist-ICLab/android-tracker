package kaist.iclab.mobiletracker.navigation

/**
 * Sealed class representing all screens in the app.
 * Used for type-safe navigation.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
}

