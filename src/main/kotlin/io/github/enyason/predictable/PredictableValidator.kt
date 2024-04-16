package io.github.enyason.io.github.enyason.predictable

import io.github.enyason.predictable.Predictable


fun Predictable.validate() {
    if (modelId.isEmpty()) throw IllegalArgumentException("Provided an empty model ID")
    if (versionId.isEmpty()) throw IllegalArgumentException("Provided an empty version ID")
    if (input.keys.isEmpty()) throw IllegalArgumentException("Predictable should have at least one input property")
}