package io.github.enyason.predictable

/**
 * Describes a model to be executed by replicate
 * @author Emmanuel Enya
 */
interface Predictable {
    val modelId: String?
    val versionId: String
    val input: Map<String, Any>
}
