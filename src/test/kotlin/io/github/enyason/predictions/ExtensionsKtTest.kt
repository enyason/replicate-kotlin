package io.github.enyason.predictions

import junit.framework.TestCase.assertEquals
import kotlin.test.Test

class ExtensionsKtTest {

    @Test
    fun `getCursor on empty string returns empty string`() {
        val emptyString = ""
        val cursor = emptyString.getCursor()
        assertEquals("", cursor)
    }

    @Test
    fun `getCursor on null string returns empty string`() {
        val nullString: String? = null
        val cursor = nullString.getCursor()
        assertEquals("", cursor)
    }

    @Test
    fun `getCursor on string with no query string returns empty string`() {
        val noQueryString = "https://api.replicate.com/v1/predictions"
        val cursor = noQueryString.getCursor()
        assertEquals("", cursor)
    }

    @Test
    fun `getCursor on string with query string but no cursor param returns empty string`() {
        val noCursorParam = "https://api.replicate.com/v1/predictions?key1=value1"
        val cursor = noCursorParam.getCursor()
        assertEquals("", cursor)
    }

    @Test
    fun `getCursor on string with query string and cursor param in the middle returns value`() {
        val cursorMiddle =
            "https://api.replicate.com/v1/predictions?key1=value1&cursor=abc123&key2=value2"
        val cursor = cursorMiddle.getCursor()
        assertEquals("abc123", cursor)
    }

    @Test
    fun `getCursor on string with query string and cursor param at the beginning returns value`() {
        val cursorBeginning = "https://api.replicate.com/v1/predictions?cursor=def456&key1=value1"
        val cursor = cursorBeginning.getCursor()
        assertEquals("def456", cursor)
    }

    @Test
    fun `getCursor on string with query string and cursor param with multiple values returns first value`() {
        val multipleCursors = "https://api.example.com/data?cursor=ghi789&key1=value1&cursor=jkl012"
        val cursor = multipleCursors.getCursor()
        assertEquals("ghi789", cursor)
    }

    @Test()
    fun `getCursor on malformed URL returns empty string`() {
        val malformedUrl = "notAUrl"
        val cursor = malformedUrl.getCursor()
        assertEquals("", cursor)
    }
}
