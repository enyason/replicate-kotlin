package io.github.enyason.predictions.predictable

/**
 * Describes a model to be executed by replicate
 * The generic type indicates the output of the [io.github.enyason.domain.models.Prediction]
 * @author Emmanuel Enya
 */
interface Predictable {
    val modelId: String?
    val versionId: String
    val input: Map<String, Any>
}
