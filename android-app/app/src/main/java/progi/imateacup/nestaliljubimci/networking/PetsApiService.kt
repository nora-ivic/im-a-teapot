package progi.imateacup.nestaliljubimci.networking

import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.model.networking.response.ListPetsResponse
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val PAGE = 1
const val PAGE_SIZE = 15

interface PetsApiService {

    @POST("authorization/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/pets")
    suspend fun getPets(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE
    ): Response<ListPetsResponse>
}