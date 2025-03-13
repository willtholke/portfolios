package portfolios.datalayer

import org.junit.jupiter.api.Test
import portfolios.TestConstants
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PortfolioTest {
    private val emptyPortfolio: Portfolio = Portfolio("Empty Portfolio")
    private val defaultPortfolio: Portfolio = Portfolio("Default Portfolio", TestConstants.initialAssets)

    @Test
    fun `Portfolio with no initialAssets has no assets`() {
        assertTrue(emptyPortfolio.assets.isEmpty())
    }

    @Test
    fun `Portfolio with initialAssets has the correct assets`() {
        val expectedAssets = TestConstants.initialAssets.toString()

        assertEquals(expectedAssets, defaultPortfolio.assets.toString())
    }

    @Test
    fun `Getting the total value of a portfolio with no assets returns 0`() {
        val expectedTotalValue = BigDecimal.ZERO

        assertEquals(expectedTotalValue, emptyPortfolio.getTotalValue())
    }

    @Test
    fun `Getting the total value of a portfolio with assets returns the correct assets`() {
        val expectedTotalValue = BigDecimal(110010)

        assertEquals(expectedTotalValue, defaultPortfolio.getTotalValue())
    }

    @Test
    fun `Getting the asset percentage of an asset in a portfolio where the total value is 0 returns 0`() {
        val expectedAssetPercentage = 0

        assertEquals(expectedAssetPercentage, emptyPortfolio.getAssetPercentage("ETH"))
    }

    @Test
    fun `Single asset yields 100 percent composition of portfolio`() {
        val portfolio = Portfolio(
            "Test Portfolio",
            listOf(Asset("USD", BigDecimal("10"), BigDecimal("1")))
        )
        val expectedAssetPercentage = 100

        assertEquals(expectedAssetPercentage, portfolio.getAssetPercentage("USD"))
    }

    @Test
    fun `Two assets with equal valuation yield equal percentage compositions of portfolio`() {
        val portfolio = Portfolio(
            "Test Portfolio",
            listOf(
                Asset("Asset1", BigDecimal("10"), BigDecimal("1")),  // 10 * 1 = 10
                Asset("Asset2", BigDecimal("5"), BigDecimal("2")),   // 5 * 2 = 10
            )
        )

        val assetPercentages = listOf(
            portfolio.getAssetPercentage("Asset1"),
            portfolio.getAssetPercentage("Asset2"),
        )
        val expectedAssetPercentage = 50

        assertEquals(100, assetPercentages.sum())
        assertEquals(expectedAssetPercentage, assetPercentages[0])
        assertEquals(expectedAssetPercentage, assetPercentages[1])
    }

    @Test
    fun `Three assets with equal valuation yield correct rounded percentage compositions of portfolio`() {
        val portfolio = Portfolio(
            "Test Portfolio",
            listOf(
                Asset("Asset1", BigDecimal("10"), BigDecimal("1")),  // 10 * 1 = 10
                Asset("Asset2", BigDecimal("5"), BigDecimal("2")),   // 5 * 2 = 10
                Asset("Asset3", BigDecimal("2"), BigDecimal("5")),   // 2 * 5 = 10
            )
        )

        val assetPercentages = listOf(
            portfolio.getAssetPercentage("Asset1"),
            portfolio.getAssetPercentage("Asset2"),
            portfolio.getAssetPercentage("Asset3")
        )

        assertEquals(100, assetPercentages.sum())
        assertEquals(34, assetPercentages[0])
        assertEquals(33, assetPercentages[1])
        assertEquals(33, assetPercentages[2])
    }

    @Test
    fun `Six assets with equal valuation yield correct rounded percentage compositions of portfolio`() {
        val portfolio = Portfolio(
            "Test Portfolio",
            listOf(
                Asset("Asset1", BigDecimal("10"), BigDecimal("1")),  // 10 * 1 = 10
                Asset("Asset2", BigDecimal("5"), BigDecimal("2")),   // 5 * 2 = 10
                Asset("Asset3", BigDecimal("2"), BigDecimal("5")),   // 2 * 5 = 10
                Asset("Asset4", BigDecimal("1"), BigDecimal("10")),  // 1 * 10 = 10
                Asset("Asset5", BigDecimal("0.1"), BigDecimal("100")), // 0.1 * 100 = 10
                Asset("Asset6", BigDecimal("0.1"), BigDecimal("100"))  // 0.1 * 100 = 10
            )
        )

        val assetPercentages = listOf(
            portfolio.getAssetPercentage("Asset1"),
            portfolio.getAssetPercentage("Asset2"),
            portfolio.getAssetPercentage("Asset3"),
            portfolio.getAssetPercentage("Asset4"),
            portfolio.getAssetPercentage("Asset5"),
            portfolio.getAssetPercentage("Asset6")
        )

        assertEquals(100, assetPercentages.sum())
        assertEquals(17, assetPercentages[0])
        assertEquals(17, assetPercentages[1])
        assertEquals(17, assetPercentages[2])
        assertEquals(17, assetPercentages[3])
        assertEquals(16, assetPercentages[4])
        assertEquals(16, assetPercentages[5])
    }

    @Test
    fun `Getting the string representation of a portfolio with no assets returns the correct string`() {
        val expectedString = """{"name": "Empty Portfolio", "total_value": 0, "assets": []}"""

        assertEquals(expectedString, emptyPortfolio.toString())
    }

    @Test
    fun `Getting the string representation of a portfolio with assets returns the correct string`() {
        val expectedString = """{"name": "Default Portfolio", "total_value": 110010, "assets": [{"symbol": "USD", """ +
                """"shares": 10, "valuation": 1, "percentage": 1}, {"symbol": "ETH", "shares": 10, "valuation": """ +
                """1000, "percentage": 9}, {"symbol": "BTC", "shares": 10, "valuation": 10000, "percentage": 90}]}"""

        assertEquals(expectedString, defaultPortfolio.toString())
    }
}
