package com.pilapil.sass.api

import com.pilapil.sass.model.ApiResponse
import com.pilapil.sass.model.AvailableTimeResponse
import com.pilapil.sass.model.BookedAppointmentsResponse
import com.pilapil.sass.model.BookingRequest
import com.pilapil.sass.model.BookingResponse
import com.pilapil.sass.model.ChatHistoryResponse
import com.pilapil.sass.model.ChatRequest
import com.pilapil.sass.model.ChatResponse
import com.pilapil.sass.model.ChatSaveRequest
import com.pilapil.sass.model.FileSubmissionRequest
import com.pilapil.sass.model.FileUploadResponse
import com.pilapil.sass.model.LoginRequest
import com.pilapil.sass.model.LoginResponse
import com.pilapil.sass.model.NotificationResponseWrapper
import com.pilapil.sass.model.ReportRequest
import com.pilapil.sass.model.Student
import com.pilapil.sass.model.StudentBookingsResponse
import com.pilapil.sass.model.StudentProfile
import com.pilapil.sass.model.SubmissionResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

//const val BASE_URL = "http://192.168.1.122:3000/api/"
//const val BASE_URL = "http://192.168.1.52:3000/api/" // Office IP
const val BASE_URL = "http://192.168.167.252:3000/api/"

//const val PYTHON_BASE_URL = "http://192.168.1.122:8000/"
//const val PYTHON_BASE_URL = "http://192.168.1.52:8000/" // Office Chatbot Backend
const val PYTHON_BASE_URL = "http://192.168.167.252:8000/"

interface ApiService {
    @POST("students/register/student")
    suspend fun registerStudent(@Body student: Student): ApiResponse

    @POST("students/login")
    suspend fun loginStudent(@Body loginRequest: LoginRequest): LoginResponse

    @POST("students/chat/save")
    suspend fun saveChat(
        @Header("Authorization") token: String,
        @Body chatRequest: ChatSaveRequest
    ): ApiResponse

    @GET("students/chat/history/{studentId}")
    suspend fun getChatHistory(
        @Path("studentId") studentId: String,
        @Header("Authorization") token: String
    ): Response<ChatHistoryResponse>

    @POST("student/submissions/submit")
    suspend fun submitFile(
        @Header("Authorization") token: String,
        @Body request: FileSubmissionRequest
    ): Response<SubmissionResponse>

    @Multipart
    @POST("student/uploads/upload")
    suspend fun uploadStudentFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("reason") reason: RequestBody
    ): FileUploadResponse



    @GET("student/notifications/responses")
    suspend fun getStudentNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationResponseWrapper>

    @POST("student/notifications/update-token")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Body fcmToken: Map<String, String>
    ): Response<ApiResponse>

    @POST("student/appointments/book")
    suspend fun bookAppointment(
        @Header("Authorization") token: String,
        @Body request: BookingRequest
    ): Response<BookingResponse>

    @GET("student/appointments/booked/{date}")
    suspend fun getBookedAppointments(
        @Path("date") date: String,
        @Header("Authorization") token: String
    ): Response<BookedAppointmentsResponse>


    @GET("appointments/availability/{schoolId}/{date}")
    suspend fun getAvailableTimeSlots(
        @Path("schoolId") schoolId: String,
        @Path("date") date: String,
        @Header("Authorization") token: String
    ): Response<AvailableTimeResponse>

    @GET("appointments/student/{studentId}")
    suspend fun getStudentBookings(
        @Path("studentId") studentId: String,
        @Header("Authorization") token: String
    ): Response<StudentBookingsResponse>

    @POST("student/report/submit")
    suspend fun submitReport(
        @Body reportRequest: ReportRequest,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("students/profile")
    suspend fun getStudentProfile(
        @Header("Authorization") token: String
    ): Response<StudentProfile>


    @PUT("students/update-profile")
    suspend fun updateStudentProfile(
        @Header("Authorization") token: String,
        @Body updateData: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ApiResponse>




    companion object {
        fun create(): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient) // ⬅️ Apply custom timeout-enabled client
                .build()
                .create(ApiService::class.java)
        }
    }
}

interface PythonApiService {
    @POST("chatbot")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse

    companion object {
        fun create(): PythonApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(PYTHON_BASE_URL)
                .client(client) // ⏱️ Apply the custom timeout and logger
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PythonApiService::class.java)
        }
    }
}











