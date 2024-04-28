package io.github.enyason.predictions.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

data class PredictionErrorResponse(
    var detail: String? = null,
    var title: String? = null,
    @SerializedName("invalid_fields")
    var invalidFields: List<Any>? = null,
    var status: Int? = null
)

fun ResponseBody.toModel(): PredictionErrorResponse? {
    val jsonString = this.string()
    return Gson().fromJson(jsonString, PredictionErrorResponse::class.java)
}
