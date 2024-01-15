package progi.imateacup.nestaliljubimci.networking

import okhttp3.RequestBody
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment
import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.CreateAdvertRequest
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import progi.imateacup.nestaliljubimci.model.networking.response.RegisterResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Advert
import progi.imateacup.nestaliljubimci.model.networking.response.AddCommentResponse
import progi.imateacup.nestaliljubimci.model.networking.response.CreateAdvertResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @POST("api/advert/create")
    suspend fun addAdvert(@Body request: CreateAdvertRequest): Response<CreateAdvertResponse>

    @POST("api/messages/{advert_id}/add")
    suspend fun addComment(
        @Path("advert_id") advertId: Int,
        @Body request: AddCommentRequest
    ): Response<AddCommentResponse>

    @POST("api/pictures/upload")
    suspend fun uploadImage(
        @Body image: RequestBody
    ): Response<String>

    @GET("api/advert/")
    suspend fun getPets(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE,
        @QueryMap filter: Map<String, String>
    ): Response<List<Pet>>

    @GET("/api/advert/my_adverts")
    suspend fun getMyPets(
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE,
    ): Response<List<Pet>>

    @GET("api/advert/{advert_id}/details")
    suspend fun getAdvertDetails(
        @Path("advert_id") advertId: Int,
    ): Response<Advert>

    @GET("api/messages/{advert_id}")
    @DELETE("/api/advert/{advert_id}/delete")
    suspend fun deleteAdvert(
        @Path("advert_id") advertId: Int,
    ): Response<Unit>

    @GET("api/messages/{advert_id}")
    suspend fun getComments(
        @Path("advert_id") advertId: Int,
        @Query("page") page: Int = PAGE,
        @Query("page_size") items: Int = PAGE_SIZE,
    ): Response<List<Comment>>
}