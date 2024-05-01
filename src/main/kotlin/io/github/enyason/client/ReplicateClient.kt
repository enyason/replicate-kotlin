package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictable.validate
import io.github.enyason.predictable.validateId
import io.github.enyason.predictions.PredictionsApi

/**
 * [ReplicateClient] is the concrete implementation of [Replicate]
 * @see Replicate
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
class ReplicateClient(
    private val predictionAPI: PredictionsApi
) : Replicate {

    override suspend fun createPrediction(predictable: Predictable): Result<Prediction> {
        return try {
            predictable.validate()
            val request = mapOf(
                "version" to predictable.versionId,
                "input" to predictable.input
            )
            val (prediction, error) = predictionAPI.createPrediction(request)
            when {
                prediction != null -> Result.success(prediction)
                else -> Result.failure(error ?: Throwable())
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun getPrediction(predictionId: String): Result<Prediction> {
        return try {
            predictionId.validateId()
            val (prediction, error) = predictionAPI.getPrediction(predictionId)
            when {
                prediction != null -> Result.success(prediction)
                else -> Result.failure(error ?: Throwable())
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun getPredictions(): Result<List<Prediction>> {
        return Result.success(emptyList())
    }

    override suspend fun cancelPrediction(predictionId: String): Result<Boolean> {
        return try {
            predictionId.validateId()
            val (isSuccess, error) = predictionAPI.cancelPrediction(predictionId)
            when {
                isSuccess -> Result.success(true)
                else -> Result.failure(error ?: Throwable())
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
