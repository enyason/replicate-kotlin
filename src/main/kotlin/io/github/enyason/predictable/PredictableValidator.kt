package io.github.enyason.predictable

fun Predictable.validate() {
    if (modelId.isEmpty()) throw IllegalArgumentException("Provided an empty model ID")
    if (versionId.isEmpty()) throw IllegalArgumentException("Provided an empty version ID")
    if (input.keys.isEmpty()) throw IllegalArgumentException("Predictable should have at least one input property")
}

fun String.validateId() {
    if (isEmpty()) throw IllegalArgumentException("Provided an empty prediction ID")
}
