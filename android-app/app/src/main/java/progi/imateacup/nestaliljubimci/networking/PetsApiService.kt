package progi.imateacup.nestaliljubimci.networking

import progi.imateacup.nestaliljubimci.model.networking.request.auth.LoginRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.RegisterRequest
import progi.imateacup.nestaliljubimci.model.networking.request.auth.AddCommentRequest
import progi.imateacup.nestaliljubimci.model.networking.response.LoginResponse
import progi.imateacup.nestaliljubimci.model.networking.response.RegisterResponse
import progi.imateacup.nestaliljubimci.model.networking.response.AdvertResponse
import progi.imateacup.nestaliljubimci.model.networking.response.AddCommentResponse
import progi.imateacup.nestaliljubimci.model.networking.response.ListCommentsResponse
import progi.imateacup.nestaliljubimci.model.networking.response.ListPetsResponse
import progi.imateacup.nestaliljubimci.model.networking.response.ShelterResponse
import progi.imateacup.nestaliljubimci.model.networking.response.PetResponse
import progi.imateacup.nestaliljubimci.model.networking.response.Pet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/advert/{id}")
    suspend fun getDetailedAdvert(
        @Path("id") advertId: String,
    ): AdvertResponse

    @GET("/advert/{id}/comments")
    suspend fun getComments(
        @Path("id") advertId: String,
    ): Response<ListCommentsResponse>

    @GET("/pet/{id}")
    suspend fun getPet(
        @Path("id") petId: String,
    ): PetResponse

    @GET("/shelter/{id}")
    suspend fun getShelter(
        @Path("id") shelterId: String,
    ): ShelterResponse

    @POST("/comment")
    suspend fun addComment(
        @Body request: AddCommentRequest
    ): Response<AddCommentResponse>
}