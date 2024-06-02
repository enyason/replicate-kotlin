package io.github.enyason.predictions.predictable

/**
 * Validates the current state of the Predictable object.
 * Throws an [IllegalArgumentException] if:
 *  - The [Predictable.versionId] is empty (using `isBlank()` for stricter validation including whitespace).
 *  - The [Predictable.input] map is empty (indicating no input properties are set).
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 */

fun Predictable.validate() {
    if (versionId.isBlank()) throw IllegalArgumentException("Provided an empty version ID")
    if (input.keys.isEmpty()) throw IllegalArgumentException("Predictable should have at least one input property")
}

/**
 * Validates the current String object as a prediction ID.
 *
 * Throws an [IllegalArgumentException] if the String is blank
 */
fun String.validateId() {
    if (isBlank()) throw IllegalArgumentException("Provided an empty prediction ID")
}
