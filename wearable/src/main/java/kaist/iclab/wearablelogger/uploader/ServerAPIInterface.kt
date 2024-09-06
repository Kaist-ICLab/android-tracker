//package kaist.iclab.wearablelogger.uploader
//
//import retrofit2.Call
//import retrofit2.http.Body
//import retrofit2.http.Headers
//import retrofit2.http.POST
//
//interface ServerAPIInterface {
//    @POST("/upload")
//    @Headers("Content-Type: plain/text")
//    suspend fun postData(
//        @Body data: String
//    ): Call<String>
//}