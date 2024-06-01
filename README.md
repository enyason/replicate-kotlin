# Replicate Kotlin Client

Replicate-Kotlin is a wrapper around [Replicate’s API](https://replicate.com/), enabling you to interact with cloud-based AI models using pure Kotlin code. This library is designed to easily integrate generative AI into Android and other kotlin supported environments.

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Example](#example)
5. [Configuration](#configuration)
6. [API Reference](#api-reference)
7. [Contributing](#contributing)
8. [License](#license)
9. [Contact](#contact)

## Overview

Replicate lets you run machine learning models with a cloud API without needing to understand the intricacies of machine learning. This client library provides a client class to interact with the [Replicate](https://replicate.com) API, offering methods for creating, retrieving, and canceling predictions.

## Features

- Initialize Replicate client with an auth token
- Create, retrieve, and list predictions
- Cancel predictions
- Await prediction completion

## Installation

Add the following dependency to your `build.gradle` file:

```groovy
implementation 'io.github.enyason:replicate-kotlin:0.1.0'
```

## Usage

### Initialize Client

You can initialize the Replicate client using an API token or a configuration object.

```kotlin
val client = Replicate.client("your-api-token")

// or

val config = ReplicateConfig("your-api-token")
val client = Replicate.client(config)
```

> [!WARNING]
> Don't store your APi token in code.
> Instead, you can explore secure storage solutions provided by your target platform(e.g.Android Secrets Gradle Plugin for Android)

### Create a Prediction

To create a prediction, you need to provide a `Predictable` instance.

```kotlin
suspend fun createPredictionExample() {
    val predictable = PhotoBackgroundRemover(input = mapOf("image" to dataUri))
    val predictionTask = client.createPrediction<String>(predictable)
    println(predictionTask.result)
}
```

### Get a Prediction

Retrieve a prediction by its ID.

```kotlin
suspend fun getPredictionExample(predictionId: String) {
    val predictionTask = client.getPrediction<String>(predictionId)
    println(prediction.output)
}
```

### Cancel a Prediction

Cancel an ongoing prediction using its ID.

```kotlin
suspend fun cancelPredictionExample(predictionId: String) {
    val result = client.cancelPrediction(predictionId)
    if (result.isSuccess) {
        println("Prediction canceled successfully")
    } else {
        println("Failed to cancel prediction: ${result.exceptionOrNull()}")
    }
}
```

### List Predictions

To be implemented.

```kotlin
fun listPredictionsExample() {
    val predictions = client.getPredictions()
    println(predictions)
}
```


### Await Prediction Result

Awaits for the completion of a Prediction Task.

```kotlin
fun awaitPredictionTaskExample() {
    val predictionTask = client.createPrediction<Any>(predictable)
    val prediction = predictionTask.await()
    println(prediction.output)
}
```

## Example
This example removes background from a photo. For the input image, you can use a remote url or a local file. However, 
the file must be a bas64 encoded string as shown in the example.
```kotlin
suspend fun main() {
    val imageFile = File(classLoader.getResource("image.jpg")!!.file)
    val imageBytes = Files.readAllBytes(imageFile.toPath())
    val encodedImage = Base64.encode(imageBytes)
    val mimeType = "image/jpeg"
    val dataUri = "data:$mimeType;base64,$encodedImage"

    val client = Replicate.client("token")
    val predictable = BgRemoval(input = mapOf("image" to dataUri))
    val task = client.createPrediction<String>(predictable)
    
    val result = task.await()
    println(result?.output)
}
```


## Configuration

You can configure the SDK behavior using the `ReplicateConfig` class.

```kotlin
data class ReplicateConfig(
    val token: String,
    val baseUrl: String = "https://api.replicate.com/v1"
)
```

## API Reference
Reference: https://replicate.com/docs/reference/http


## Contributing

Contributions are welcome! Please read our [contributing guidelines](CONTRIBUTING) for more details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For support or questions, please contact us at [enyason95@gmail.com](mailto:enyason95@gmail.com).