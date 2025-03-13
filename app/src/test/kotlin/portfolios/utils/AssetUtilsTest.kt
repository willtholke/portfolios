package portfolios.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AssetUtilsTest {
    @Test
    fun `toUpper converts all symbols in a list to uppercase`() {
        val input = listOf("btc", "EtH", "uSD")
        val expected = listOf("BTC", "ETH", "USD")
        val result = AssetUtils.toUpper(input)
        assertEquals(expected, result)
    }

    @Test
    fun `hasUniqueAssetSymbols returns true for a list with unique symbols`() {
        val uniqueSymbols = listOf("BTC", "ETH", "USD")
        assertTrue(AssetUtils.hasUniqueAssetSymbols(uniqueSymbols))
    }

    @Test
    fun `hasUniqueAssetSymbols returns false for a list with duplicate symbols`() {
        val duplicateSymbols = listOf("BTC", "ETH", "BTC")
        assertFalse(AssetUtils.hasUniqueAssetSymbols(duplicateSymbols))
    }
}
