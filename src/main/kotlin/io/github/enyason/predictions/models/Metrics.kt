package io.github.enyason.predictions.models

import com.google.gson.annotations.SerializedName

data class Metrics(
    @SerializedName("predict_time")
    var predictTime: Double? = null
)
