package kaist.iclab.tracker.sync.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.Realtime

/**
 * Simple function to create a configured Supabase client.
 * This centralizes Supabase setup and makes it reusable across projects.
 */

/**
 * Creates a configured Supabase client with PostgREST support.
 * 
 * @param supabaseUrl The Supabase project URL
 * @param supabaseKey The Supabase anon key
 * @return Configured Supabase client ready for database operations
 */
fun createSupabaseClient(supabaseUrl: String, supabaseKey: String): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Postgrest)
        install(Auth)
        install(Realtime)
    }
}
