package io.github.enyason.predictable

interface Predictable {
    val modelId: String
    val versionId: String
    val input: Map<String, Any>
}
