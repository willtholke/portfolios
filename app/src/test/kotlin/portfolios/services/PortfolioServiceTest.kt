package portfolios.services

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import portfolios.TestConstants
import portfolios.datalayer.Asset
import portfolios.datalayer.Portfolio
import portfolios.datalayer.mockdb.MockDatabase
import portfolios.exceptions.AssetNotFoundException
import portfolios.exceptions.PortfolioNotFoundException
import java.math.BigDecimal
import javax.ws.rs.BadRequestException
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class PortfolioServiceTest {
    @Mock
    private lateinit var mockedValuationCacheService: ValuationCacheService

    @Mock
    private lateinit var mockedMockDatabase: MockDatabase

    @Mock
    private lateinit var mockedValuationServiceBTC: AssetValuationServiceBTCImpl

    @Mock
    private lateinit var mockedValuationServiceETH: AssetValuationServiceETHImpl

    @Mock
    private lateinit var mockedPortfolioService: PortfolioService

    private lateinit var portfolioService: PortfolioService

    private val emptyAssetSymbol: String = ""
    private val invalidAssetSymbol: String = "123"

    @BeforeEach
    fun setUp() {
        portfolioService = PortfolioService(
            mockDatabase = mockedMockDatabase
        )
        mockedPortfolioService = PortfolioService(  // mockedPortfolioService is used exclusively in the last test
            valuationServiceBTC = mockedValuationServiceBTC,
            valuationServiceETH = mockedValuationServiceETH,
            valuationCacheService = mockedValuationCacheService
        )
    }

    @Test
    fun `Getting all portfolios returns a list of all portfolios`() {
        `when`(mockedMockDatabase.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)
        val portfolios = portfolioService.getAllPortfolios()

        assertEquals(TestConstants.mockedPortfolios.size, portfolios.size)
        portfolios.forEachIndexed { index, portfolio ->
            assertEquals(TestConstants.mockedPortfolios[index].name, portfolio.name)
            assertEquals(TestConstants.mockedPortfolios[index].assets.size, portfolio.assets.size)
        }
    }

    @Test
    fun `Getting all portfolios when there are no portfolios returns an empty list`() {
        `when`(mockedMockDatabase.getAllPortfolios()).thenReturn(emptyList())
        val portfolios = portfolioService.getAllPortfolios()

        assertEquals(emptyList(), portfolios.toList())
    }

    @Test
    fun `Getting a portfolio by name that exists returns the correct portfolio`() {
        `when`(mockedMockDatabase.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME))
            .thenReturn(TestConstants.mockedPortfolios[0])
        val portfolio = portfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(TestConstants.DEFAULT_PORTFOLIO_NAME, portfolio.name)
        assertEquals(TestConstants.mockedPortfolios[0].assets.size, portfolio.assets.size)
    }

    @Test
    fun `Getting a portfolio by name that doesn't exist throws PortfolioNotFoundException`() {
        `when`(mockedMockDatabase.getPortfolioByName(TestConstants.NON_EXISTENT_PORTFOLIO_NAME))
            .thenReturn(null)
        val exception = assertThrows<PortfolioNotFoundException> {
            portfolioService.getPortfolioByName(TestConstants.NON_EXISTENT_PORTFOLIO_NAME)
        }

        assertEquals(
            "Portfolio '${TestConstants.NON_EXISTENT_PORTFOLIO_NAME}' not found.",
            exception.message
        )
    }

    @Test
    fun `Filtering a portfolio by asset with invalid characters throws BadRequestException`() {
        val exception = assertThrows<BadRequestException> {
            portfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                listOf(invalidAssetSymbol)
            )
        }

        assertEquals(
            "Asset symbol '$invalidAssetSymbol' contains invalid characters.",
            exception.message
        )
    }

    @Test
    fun `Filtering a portfolio by asset with an empty asset symbol throws BadRequestException`() {
        val exception = assertThrows<BadRequestException> {
            portfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                listOf(emptyAssetSymbol)
            )
        }

        assertEquals("Asset symbol cannot be empty.", exception.message)
    }

    @Test
    fun `Filtering a portfolio by asset with a nonexistent asset throws AssetNotFoundException`() {
        val exception = assertThrows<AssetNotFoundException> {
            portfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                listOf(TestConstants.NON_EXISTENT_ASSET_SYMBOL)
            )
        }

        assertEquals(
            "Asset '${TestConstants.NON_EXISTENT_ASSET_SYMBOL}' not found in portfolio " +
                    "'${TestConstants.DEFAULT_PORTFOLIO_NAME}'.", exception.message
        )
    }

    @Test
    fun `Filtering a portfolio by asset with a duplicate asset throws BadRequestException`() {
        val duplicateSymbols = listOf("BTC", "BTC")
        val exception = assertThrows<BadRequestException> {
            portfolioService.filterPortfolioByAsset(TestConstants.mockedPortfolios[0], duplicateSymbols)
        }

        assertEquals("Cannot filter portfolio by duplicate asset symbols.", exception.message)
    }

    @Test
    fun `Getting a portfolio from a single asset filter returns the correctly filtered portfolio`() {
        val filteredPortfolio = portfolioService.filterPortfolioByAsset(
            TestConstants.mockedPortfolios[0],
            listOf("USD")
        )

        assertEquals(
            Portfolio(
                TestConstants.DEFAULT_PORTFOLIO_NAME, listOf(
                    Asset("USD", BigDecimal("10"), BigDecimal("1"))
                )
            ).toString(),
            filteredPortfolio.toString()
        )
    }

    @Test
    fun `Getting a portfolio from a single asset filter using a nonexistent asset returns an AssetNotFoundException`() {
        val exception = assertThrows<AssetNotFoundException> {
            portfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                listOf(TestConstants.NON_EXISTENT_ASSET_SYMBOL)
            )
        }

        assertEquals(
            "Asset '${TestConstants.NON_EXISTENT_ASSET_SYMBOL}' not found in portfolio " +
                    "'${TestConstants.DEFAULT_PORTFOLIO_NAME}'.",
            exception.message
        )
    }

    @Test
    fun `Getting a portfolio from a multi-asset filter returns the correctly filtered portfolio`() {
        val filteredPortfolio = portfolioService.filterPortfolioByAsset(
            TestConstants.mockedPortfolios[0],
            listOf("USD", "BTC")
        )

        val filteredAssetSymbols = filteredPortfolio.assets.map { it.symbol }
        val expectedAssetSymbols = listOf("USD", "BTC")

        assertEquals(expectedAssetSymbols, filteredAssetSymbols)
    }

    @Test
    fun `Getting a portfolio from a multi-asset filter using a nonexistent asset returns an AssetNotFoundException`() {
        val exception = assertThrows<AssetNotFoundException> {
            portfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0], listOf(
                    "USD", "ETH",
                    TestConstants.NON_EXISTENT_ASSET_SYMBOL
                )
            )
        }

        assertEquals(
            "Asset '${TestConstants.NON_EXISTENT_ASSET_SYMBOL}' not found in portfolio " +
                    "'${TestConstants.DEFAULT_PORTFOLIO_NAME}'.",
            exception.message
        )
    }

    @Test
    fun `Getting all portfolios from a single asset filter returns the correctly filtered portfolio`() {
        val filteredPortfolio = portfolioService.filterPortfoliosByAsset(TestConstants.mockedPortfolios, listOf("USD"))

        assertEquals(
            listOf(
                Portfolio(
                    TestConstants.DEFAULT_PORTFOLIO_NAME, listOf(
                        Asset("USD", BigDecimal("10"), BigDecimal("1"))
                    )
                ),
                Portfolio(
                    "Ale's Portfolio", listOf(
                        Asset("USD", BigDecimal("100"), BigDecimal("1"))
                    )
                )
            ).toString(),
            filteredPortfolio.toString()
        )
    }

    @Test
    fun `Getting all portfolios from a single asset filter using a nonexistent asset returns an AssetNotFoundException`() {
        val exception = assertThrows<AssetNotFoundException> {
            portfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios,
                listOf(TestConstants.NON_EXISTENT_ASSET_SYMBOL)
            )
        }

        assertEquals(
            "Asset '${TestConstants.NON_EXISTENT_ASSET_SYMBOL}' not found in portfolio " +
                    "'${TestConstants.DEFAULT_PORTFOLIO_NAME}'.",
            exception.message
        )
    }

    @Test
    fun `Getting all portfolios from a multi-asset filter returns the correctly filtered portfolios`() {
        val filteredPortfolio = portfolioService.filterPortfoliosByAsset(
            TestConstants.mockedPortfolios,
            listOf("USD", "BTC")
        )

        val filteredAssetSymbols = filteredPortfolio.map { it.assets.map { asset -> asset.symbol } }
        val expectedAssetSymbols = listOf(listOf("USD", "BTC"), listOf("USD", "BTC"))

        assertEquals(expectedAssetSymbols, filteredAssetSymbols)
    }

    @Test
    fun `Getting all portfolios from a multi-asset filter using a nonexistent asset returns an AssetNotFoundException`() {
        val exception = assertThrows<AssetNotFoundException> {
            portfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios, listOf(
                    "USD", "ETH",
                    TestConstants.NON_EXISTENT_ASSET_SYMBOL
                )
            )
        }

        assertEquals(
            "Asset '${TestConstants.NON_EXISTENT_ASSET_SYMBOL}' not found in portfolio " +
                    "'${TestConstants.DEFAULT_PORTFOLIO_NAME}'.", exception.message
        )
    }

    @Test
    fun `Updating a portfolio's asset valuations calls the valuationCache correctly`() {
        `when`(mockedValuationCacheService.getValuation("BTC", mockedValuationServiceBTC)).thenReturn(
            BigDecimal("1")
        )
        `when`(mockedValuationCacheService.getValuation("ETH", mockedValuationServiceETH)).thenReturn(
            BigDecimal("1")
        )

        mockedPortfolioService.updateAssetValuations(TestConstants.mockedPortfolios[0])

        verify(mockedValuationCacheService).getValuation("BTC", mockedValuationServiceBTC)
        verify(mockedValuationCacheService).getValuation("ETH", mockedValuationServiceETH)
    }
}
