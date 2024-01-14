package progi.imateacup.nestaliljubimci.networking

import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import progi.imateacup.nestaliljubimci.model.networking.response.RegisterResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.model.networking.response.AddCommentResponse
import progi.imateacup.nestaliljubimci.model.networking.response.ListCommentsResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

//DEFAULT VALUES
const val PAGE = 1
const val PAGE_SIZE = 15

interface PetsApiService {

    @POST("api/authorization/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/authorization/signup")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/advert/")
    suspend fun getPets(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE,
        @QueryMap filter: Map<String, String>
    ): Response<List<Pet>>

    @GET("api/authorization/shelters")
    suspend fun getShelters(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE
    ): Response<List<ShelterResponse>>

    @GET("api/advert/{advert_id}/details")
    suspend fun getAdvertDetails(
        @Path("advert_id") advertId: Int,
    ): Response<Advert>

    @GET("api/messages/{advert_id}/fetch")
    suspend fun getComments(
        @Path("advert_id") advertId: Int,
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE,
    ): Response<ListCommentsResponse>

    @POST("/comment")
    suspend fun addComment(
        @Body request: AddCommentRequest
    ): Response<AddCommentResponse>
}