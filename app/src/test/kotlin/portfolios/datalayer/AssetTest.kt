package portfolios.datalayer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class AssetTest {
    private lateinit var defaultAsset: Asset

    @BeforeEach
    fun setUp() {
        defaultAsset = Asset("Default Asset", BigDecimal(10), BigDecimal(100))
    }

    @Test
    fun `Getting shares returns correct value`() {
        val expectedShares = BigDecimal(10)

        assertEquals(expectedShares, defaultAsset.shares)
    }

    @Test
    fun `Setting shares to a positive value sets the correct value`() {
        val expectedShares = BigDecimal(20)

        defaultAsset.shares = expectedShares

        assertEquals(expectedShares, defaultAsset.shares)
    }

    @Test
    fun `Setting shares to zero throws IllegalArgumentException`() {
        val zeroShares = BigDecimal.ZERO

        assertThrows<IllegalArgumentException> {
            defaultAsset.shares = zeroShares
        }
    }

    @Test
    fun `Setting shares to a negative value throws IllegalArgumentException`() {
        val negativeShares = BigDecimal(-1)

        assertThrows<IllegalArgumentException> {
            defaultAsset.valuation = negativeShares
        }
    }

    @Test
    fun `Getting valuation returns correct value`() {
        val expectedValuation = BigDecimal(100)

        assertEquals(expectedValuation, defaultAsset.valuation)
    }

    @Test
    fun `Setting valuation to a positive value sets the correct value`() {
        val expectedValuation = BigDecimal(200)
        defaultAsset.valuation = expectedValuation

        assertEquals(expectedValuation, defaultAsset.valuation)
    }

    @Test
    fun `Setting valuation to zero sets the correct value`() {
        val expectedValuation = BigDecimal.ZERO

        defaultAsset.valuation = expectedValuation
        assertEquals(expectedValuation, defaultAsset.valuation)
    }

    @Test
    fun `Setting valuation to a negative value throws IllegalArgumentException`() {
        val negativeValuation = BigDecimal(-1)

        val exception = assertThrows<IllegalArgumentException> {
            defaultAsset.valuation = negativeValuation
        }

        assertEquals("Asset valuation must be non-negative.", exception.message)
    }

    @Test
    fun `Setting valuation of USD throws IllegalArgumentException`() {
        val asset = Asset("USD", BigDecimal("10.123456"), BigDecimal("1.00000"))
        val expectedValuation = BigDecimal(2)

        val exception = assertThrows<IllegalArgumentException> {
            asset.valuation = expectedValuation
        }

        assertEquals("Cannot set valuation of asset 'USD'.", exception.message)
    }

    @Test
    fun `Initializing an asset with USD sets the shares scale to 2 and valuation to 1`() {
        val asset = Asset("USD", BigDecimal("10.123456"), BigDecimal("1.00000"))

        assertEquals(BigDecimal("10.12"), asset.shares)
        assertEquals(BigDecimal.ONE, asset.valuation)
    }

    @Test
    fun `Getting value returns correct value`() {
        val expectedValue = BigDecimal(1000)

        assertEquals(expectedValue, defaultAsset.value)
    }

    @Test
    fun `Getting the string representation of an asset returns the correct string`() {
        val expectedString = """{"symbol": "Default Asset", "shares": 10, "valuation": 100}"""

        assertEquals(expectedString, defaultAsset.toString())
    }
}
