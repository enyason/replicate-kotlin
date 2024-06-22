package io.github.enyason.predictions

import io.github.enyason.predictions.models.PredictionDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The main interface for communicating with [Replicate's](https://replicate.com) Predictions API
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 */
interface PredictionsApiService {
    companion object {
        const val ENDPOINT = "predictions"
    }

    @POST(ENDPOINT)
    @JvmSuppressWildcards
    suspend fun createPrediction(
        @Body predictionRequest: Map<String, Any>,
    ): ResponseBody

    @GET("$ENDPOINT/{predictionId}")
    suspend fun getPrediction(
        @Path("predictionId") predictionId: String,
    ): ResponseBody

    @POST("$ENDPOINT/{predictionId}/cancel")
    suspend fun cancelPrediction(
        @Path("predictionId") predictionId: String,
    ): Response<Unit>

    @POST("models/{modelOwner}/{modelName}/predictions")
    @JvmSuppressWildcards
    suspend fun createPredictionWithModel(
        @Path("modelOwner") modelOwner: String,
        @Path("modelName") modelName: String,
        @Body predictionRequest: Map<String, Any>,
    ): Response<PredictionDTO<Any>>

    @POST("deployments/{deploymentOwner}/{deploymentName}/predictions")
    @JvmSuppressWildcards
    suspend fun createPredictionWithDeployment(
        @Path("deploymentOwner") deploymentOwner: String,
        @Path("deploymentName") deploymentName: String,
        @Body predictionRequest: Map<String, Any>,
    ): Response<PredictionDTO<Any>>

    @GET(ENDPOINT)
    suspend fun listPredictions(
        @Query("cursor") cursor: String?,
    ): ResponseBody
}
