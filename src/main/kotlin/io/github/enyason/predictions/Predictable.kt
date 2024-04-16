package io.github.enyason.predictions

interface Predictable {
    val modelId: String
    val versionId: String
    val input: Map<String, Any>
}
