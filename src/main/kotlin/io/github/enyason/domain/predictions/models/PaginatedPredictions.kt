package io.github.enyason.domain.predictions.models

/**
 * [PaginatedPredictions] is a paginated list of predictions retrieved from the API.
 *
 * - [next]: An optional string representing the cursor pointing to the next page of results.
 *      If `null`, it indicates there are no more pages.
 * - [previous]: An optional string representing the cursor pointing to the previous page of results.
 *      If `null`, it indicates that this is the first page.
 * - [results]: A list of [Prediction] objects containing the actual prediction data for this page.
 *      Each prediction can be of any type (`Any`) depending on the prediction model used.
 *
 *  @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 *
 */

data class PaginatedPredictions(
    val next: String?,
    val previous: String?,
    val results: List<Prediction<Any>>
) {
    /**
     * @return `true` if there are more pages
     */
    fun hasNext(): Boolean = !next.isNullOrBlank()

    /**
     * @return `true` if there is/are previous pages
     */
    fun hasPrevious(): Boolean = !previous.isNullOrBlank()
}
