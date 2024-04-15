package com.github.enyason

interface Predictable {
    val modelId: String
    val versionId: String
    val input: Map<String, Any>
}