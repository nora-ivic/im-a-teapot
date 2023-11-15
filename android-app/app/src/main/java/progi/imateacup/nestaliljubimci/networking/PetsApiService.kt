package progi.imateacup.nestaliljubimci.networking

import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.model.networking.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//DEFAULT VALUES
const val PAGE = 1
const val PAGE_SIZE = 15

interface PetsApiService {

    @POST("api/authorization/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/authorization/signup")
    suspend fun register(@Body request: RegisterRequest) : Response<RegisterResponse>

    @GET("api/advert/")
    suspend fun getPets(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE
    ): Response<List<Pet>>
}