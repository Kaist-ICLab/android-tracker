package kaist.iclab.tracker.data.old

import retrofit2.Call
import retrofit2.http.*

interface ServerRetrofitAPI {
    // 서버 연결 확인 (HEAD 요청)
    @HEAD(".")
    fun checkServerConnection(): Call<Void>

    // 그룹 ID 목록 가져오기
//    @GET("groups/ids")
//    fun getGroupIds(): Call<List<String>>
//
//    // 그룹 상태와 설정 가져오기
//    @GET("groups/{name}/state-config")
//    fun getGroupStateNConfig(@Path("name") name: String): Call<Map<String, StateConfigResponse>>
}
