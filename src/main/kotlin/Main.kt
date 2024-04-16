package io.github.enyason

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.io.github.enyason.singleentry.trainings.createTraining
import io.github.enyason.multipleentry.predictions.PredictionsApi
import io.github.enyason.multipleentry.predictions.createPrediction
import io.github.enyason.multipleentry.trainings.TrainingsApi
import io.github.enyason.multipleentry.trainings.createTraining
import io.github.enyason.singleentry.ReplicateApi
import io.github.enyason.singleentry.predictions.createPrediction
import kotlinx.coroutines.runBlocking

fun main() {
    println("Hello World!")

    // ======= START: Sample usage of Single entry ======= //

    // Initialize with ReplicateConfig
    val replicate = ReplicateApi(ReplicateConfig(apiToken = "token"))

    // Or initialize with apiToken only
    //val replicate = ReplicateApi(apiToken = "token")

    // User is interested in Predictions API
    runBlocking { replicate.createPrediction() }

    // User is interested in Trainings API
    runBlocking { replicate.createTraining() }

    // ======= END: Sample usage of Single entry ======= //



    // ======= START: Sample usage of Multiple entry ======= //

    // User is only interested in Predictions API
    // Initialize with ReplicateConfig
    val predictions = PredictionsApi(ReplicateConfig(apiToken = "token"))

    // Or initialize with apiToken only
    //val predictions = PredictionsApi(apiToken = "token")

    // Call a Prediction method
    runBlocking { predictions.createPrediction() }


    // User is only interested in Trainings API
    // Initialize with ReplicateConfig
    val trainings = TrainingsApi(ReplicateConfig(apiToken = "token"))

    // Or initialize with apiToken only
    //val trainings = TrainingsApi(apiToken = "token")

    // Call a Training method
    runBlocking { trainings.createTraining() }

    // ======= END: Sample usage of Multiple entry ======= //

}
