package io.github.enyason.predictions

import junit.framework.TestCase.assertEquals
import kotlin.test.Test
import kotlin.test.assertNull

class ExtensionsKtTest {
    @Test
    fun `getCursor on empty string returns null`() {
        val emptyString = ""
        val cursor = emptyString.getCursor()
        assertNull(cursor)
    }

    @Test
    fun `getCursor on null string returns null`() {
        val nullString: String? = null
        val cursor = nullString.getCursor()
        assertNull(cursor)
    }

    @Test
    fun `getCursor on string with no query string returns null`() {
        val noQueryString = "https://api.replicate.com/v1/predictions"
        val cursor = noQueryString.getCursor()
        assertNull(cursor)
    }

    @Test
    fun `getCursor on string with query string but no cursor param returns null`() {
        val noCursorParam = "https://api.replicate.com/v1/predictions?key1=value1"
        val cursor = noCursorParam.getCursor()
        assertNull(cursor)
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
    fun `getCursor on malformed URL returns null`() {
        val malformedUrl = "notAUrl"
        val cursor = malformedUrl.getCursor()
        assertNull(cursor)
    }
}
