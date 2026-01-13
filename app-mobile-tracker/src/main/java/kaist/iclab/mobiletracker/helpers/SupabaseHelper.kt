package kaist.iclab.mobiletracker.helpers

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kaist.iclab.mobiletracker.config.AppConfig

/**
 * Direct Supabase integration using the supabase-kt library
 * This is a general helper for Supabase operations.
 * For specific data operations, use services in the services package.
 */
class SupabaseHelper {
    val supabaseClient: SupabaseClient = createSupabaseClient(
        supabaseUrl = AppConfig.SUPABASE_URL,
        supabaseKey = AppConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)  // Database operations
        install(Realtime)  // Real-time subscriptions
        install(Auth)  // Authentication operations
    }
}
