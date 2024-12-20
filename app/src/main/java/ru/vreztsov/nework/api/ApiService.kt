package ru.vreztsov.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.vreztsov.nework.auth.AuthState
import ru.vreztsov.nework.dto.MediaResponse
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.RandomDataClass
import ru.vreztsov.nework.dto.User

interface ApiService {

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthState>

    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>


    @POST("posts")
    suspend fun saveRandom(@Body obj: RandomDataClass): Response<RandomDataClass>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<MediaResponse>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>


}