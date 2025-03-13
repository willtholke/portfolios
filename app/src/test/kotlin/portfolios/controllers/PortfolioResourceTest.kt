package portfolios.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import portfolios.TestConstants
import portfolios.datalayer.Asset
import portfolios.datalayer.Portfolio
import portfolios.exceptions.AssetNotFoundException
import portfolios.exceptions.PortfolioNotFoundException
import portfolios.exceptions.ResourceAlreadyExistsException
import portfolios.services.PortfolioService
import java.math.BigDecimal
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class PortfolioResourceTest {
    @Mock
    private lateinit var mockedPortfolioService: PortfolioService

    private lateinit var portfolioResource: PortfolioResource

    private val assetSymbolFilter: List<String> = listOf("USD")
    private val assetSymbolsFilter: List<String> = listOf("USD", "BTC")
    private val nonExistentAssetSymbolFilter: List<String> = listOf(TestConstants.NON_EXISTENT_ASSET_SYMBOL)

    @BeforeEach
    fun setUp() {
        portfolioResource = PortfolioResource(mockedPortfolioService)
    }

    @Test
    fun `Creating an empty portfolio returns 201 with correct message`() {
        doNothing().`when`(mockedPortfolioService).createPortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        val response = portfolioResource.createEmptyPortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.CREATED.statusCode, response.status)
        assertEquals("Created portfolio '${TestConstants.DEFAULT_PORTFOLIO_NAME}'.", response.entity)
    }

    @Test
    fun `Creating an empty portfolio returns 409 when the service layer throws a ResourceAlreadyExistsException`() {
        `when`(mockedPortfolioService.createPortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenThrow(
            ResourceAlreadyExistsException("")
        )

        val response = portfolioResource.createEmptyPortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.CONFLICT.statusCode, response.status)
    }

    @Test
    fun `Getting all portfolios returns 200 with correct data`() {
        `when`(mockedPortfolioService.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)

        val response = portfolioResource.getAllPortfolios()

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(TestConstants.mockedPortfolios.toString(), response.entity)
    }

    @Test
    fun `Getting all portfolios returns 404 when the service layer throws an AssetNotFoundException`() {
        `when`(mockedPortfolioService.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)
        `when`(
            mockedPortfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios, nonExistentAssetSymbolFilter
            )
        ).thenThrow(AssetNotFoundException(""))

        val response = portfolioResource.getAllPortfolios(nonExistentAssetSymbolFilter)

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
    }

    @Test
    fun `Getting all portfolios returns 400 when the service layer throws a BadRequestException`() {
        `when`(mockedPortfolioService.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)
        `when`(
            mockedPortfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios, listOf("")
            )
        ).thenThrow(BadRequestException(""))

        val response = portfolioResource.getAllPortfolios(listOf(""))

        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Getting a portfolio returns 404 when the service layer throws an AssetNotFoundException`() {
        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )
        `when`(
            mockedPortfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0], nonExistentAssetSymbolFilter
            )
        ).thenThrow(AssetNotFoundException(""))

        val response = portfolioResource.getPortfolioByName(
            TestConstants.DEFAULT_PORTFOLIO_NAME, nonExistentAssetSymbolFilter
        )

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
    }

    @Test
    fun `Getting a portfolio returns 400 when the service layer throws a BadRequestException`() {
        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )
        `when`(
            mockedPortfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0], listOf("")
            )
        ).thenThrow(BadRequestException(""))

        val response = portfolioResource.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME, listOf(""))

        assertEquals(Response.Status.BAD_REQUEST.statusCode, response.status)
    }

    @Test
    fun `Deleting an existing portfolio returns 200 with correct message`() {
        doNothing().`when`(mockedPortfolioService).deletePortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        val response = portfolioResource.deletePortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals("Deleted portfolio '${TestConstants.DEFAULT_PORTFOLIO_NAME}'.", response.entity)

    }

    @Test
    fun `Deleting a portfolio returns 404 when the service layer throws a PortfolioNotFoundException`() {
        `when`(mockedPortfolioService.deletePortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenThrow(
            PortfolioNotFoundException("")
        )

        val response = portfolioResource.deletePortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
    }

    @Test
    fun `Getting all portfolios from a single asset filter returns 200 with correctly filtered portfolios`() {
        val filteredPortfolios: List<Portfolio> = listOf(
            Portfolio(
                "Will's Portfolio", listOf(
                    Asset("USD", BigDecimal("10"), BigDecimal("1")),
                )
            ),
            Portfolio(
                "Ale's Portfolio", listOf(
                    Asset("USD", BigDecimal("100"), BigDecimal("1")),
                )
            )
        )

        `when`(mockedPortfolioService.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)
        `when`(
            mockedPortfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios,
                assetSymbolFilter
            )
        ).thenReturn(filteredPortfolios)

        val response = portfolioResource.getAllPortfolios(assetSymbolFilter)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(filteredPortfolios.toString(), response.entity)
    }

    @Test
    fun `Getting all portfolios from a multi-asset filter returns 200 with correctly filtered portfolios`() {
        val filteredPortfolios: List<Portfolio> = listOf(
            Portfolio(
                "Will's Portfolio", listOf(
                    Asset("USD", BigDecimal("10"), BigDecimal("1")),
                    Asset("BTC", BigDecimal("10"), BigDecimal("10000"))
                )
            ),
            Portfolio(
                "Ale's Portfolio", listOf(
                    Asset("USD", BigDecimal("100"), BigDecimal("1")),
                    Asset("BTC", BigDecimal("100"), BigDecimal("10000"))
                )
            )
        )

        `when`(mockedPortfolioService.getAllPortfolios()).thenReturn(TestConstants.mockedPortfolios)
        `when`(
            mockedPortfolioService.filterPortfoliosByAsset(
                TestConstants.mockedPortfolios,
                assetSymbolsFilter
            )
        ).thenReturn(filteredPortfolios)

        val response = portfolioResource.getAllPortfolios(assetSymbolsFilter)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(filteredPortfolios.toString(), response.entity)
    }

    @Test
    fun `Getting a single portfolio returns 200 with correct data`() {
        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )

        val response = portfolioResource.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(TestConstants.mockedPortfolios[0].toString(), response.entity)
    }

    @Test
    fun `Getting a portfolio from a single asset filter returns 200 with correctly filtered portfolio`() {
        val filteredPortfolio = Portfolio(
            "Will's Portfolio", listOf(
                Asset("USD", BigDecimal("10"), BigDecimal("1")),
            )
        )

        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )
        `when`(
            mockedPortfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                assetSymbolFilter
            )
        ).thenReturn(filteredPortfolio)

        val response =
            portfolioResource.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME, assetSymbolFilter)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(filteredPortfolio.toString(), response.entity)
    }

    @Test
    fun `Getting a portfolio from a multi-asset filter returns 200 with correctly filtered portfolio`() {
        val filteredPortfolio = Portfolio(
            "Will's Portfolio", listOf(
                Asset("USD", BigDecimal("10"), BigDecimal("1")),
                Asset("BTC", BigDecimal("10"), BigDecimal("10000"))
            )
        )

        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )
        `when`(
            mockedPortfolioService.filterPortfolioByAsset(
                TestConstants.mockedPortfolios[0],
                assetSymbolsFilter
            )
        ).thenReturn(filteredPortfolio)

        val response =
            portfolioResource.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME, assetSymbolsFilter)

        assertEquals(Response.Status.OK.statusCode, response.status)
        assertEquals(filteredPortfolio.toString(), response.entity)
    }

    @Test
    fun `Getting the total value of a portfolio returns 200 with correct data`() {
        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)).thenReturn(
            TestConstants.mockedPortfolios[0]
        )

        val response = portfolioResource.getTotalValue(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertEquals(Response.Status.OK.statusCode, response.status)
    }

    @Test
    fun `Getting the total value of a portfolio returns 404 when the service layer throws a PortfolioNotFoundException`() {
        `when`(mockedPortfolioService.getPortfolioByName(TestConstants.NON_EXISTENT_PORTFOLIO_NAME)).thenThrow(
            PortfolioNotFoundException("")
        )

        val response = portfolioResource.getTotalValue(TestConstants.NON_EXISTENT_PORTFOLIO_NAME)

        assertEquals(Response.Status.NOT_FOUND.statusCode, response.status)
    }
}
