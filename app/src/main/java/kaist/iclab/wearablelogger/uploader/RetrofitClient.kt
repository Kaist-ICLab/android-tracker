package kaist.iclab.wearablelogger.uploader

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    fun getRetrofit(): Retrofit{
        retrofit?.let{
            return retrofit!!
        }
        val builder  = Retrofit.Builder()
            .baseUrl("http://143.248.53.113:3000")
            .addConverterFactory(GsonConverterFactory.create())
        retrofit = builder.build()
        return retrofit!!
    }
}