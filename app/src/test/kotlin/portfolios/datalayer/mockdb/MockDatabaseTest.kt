package portfolios.datalayer.mockdb

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import portfolios.TestConstants
import portfolios.datalayer.Asset
import portfolios.datalayer.Portfolio
import java.math.BigDecimal
import kotlin.test.*

class MockDatabaseTest {
    private lateinit var mockDatabase: MockDatabase

    private var numPortfolios = TestConstants.mockedPortfolios.size

    @BeforeEach
    fun setUp() {
        mockDatabase = MockDatabase()
    }

    @Test
    fun `getAllPortfolios returns all portfolios`() {
        val portfolios = mockDatabase.getAllPortfolios()

        assertEquals(numPortfolios, portfolios.size)
    }

    @Test
    fun `getPortfolioByName returns the correct portfolio when it exists`() {
        val portfolio = mockDatabase.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertNotNull(portfolio)
        assertEquals(TestConstants.DEFAULT_PORTFOLIO_NAME, portfolio.name)
    }

    @Test
    fun `getPortfolioByName returns null when the portfolio does not exist`() {
        val portfolio = mockDatabase.getPortfolioByName(TestConstants.NON_EXISTENT_PORTFOLIO_NAME)

        assertNull(portfolio)
    }

    @Test
    fun `createPortfolio does not add a portfolio when it already exists`() {
        val result = mockDatabase.createPortfolio(TestConstants.mockedPortfolios[0])

        assertFalse(result)
    }

    @Test
    fun `createPortfolio adds a new portfolio when it does not exist`() {
        val newPortfolio = Portfolio("New Portfolio", listOf(
            Asset("USD", BigDecimal("50"), BigDecimal("1"))))
        val result = mockDatabase.createPortfolio(newPortfolio)

        assertTrue(result)
        assertNotNull(mockDatabase.getPortfolioByName("New Portfolio"))
    }

    @Test
    fun `deletePortfolio removes a portfolio when it exists`() {
        val result = mockDatabase.deletePortfolio(TestConstants.DEFAULT_PORTFOLIO_NAME)

        assertTrue(result)
        assertNull(mockDatabase.getPortfolioByName(TestConstants.DEFAULT_PORTFOLIO_NAME))
    }

    @Test
    fun `deletePortfolio does nothing when the portfolio does not exist`() {
        val result = mockDatabase.deletePortfolio(TestConstants.NON_EXISTENT_PORTFOLIO_NAME)

        assertFalse(result)
    }
}
