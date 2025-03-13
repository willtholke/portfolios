package portfolios.datalayer.mockdb

import portfolios.datalayer.Asset
import portfolios.datalayer.Portfolio
import java.math.BigDecimal

/**
 * This class serves as a mock database for storing portfolios in memory.
 */
class MockDatabase {
    private val portfolios = mutableListOf<Portfolio>()

    init {
        portfolios.add(
            Portfolio(
                "Will's Portfolio", listOf(
                    Asset("USD", BigDecimal("10"), BigDecimal("1")),
                    Asset("ETH", BigDecimal("10"), BigDecimal("1000")),
                    Asset("BTC", BigDecimal("10"), BigDecimal("10000")),
                )
            )
        )
        portfolios.add(
            Portfolio(
                "Ale's Portfolio", listOf(
                    Asset("USD", BigDecimal("100"), BigDecimal("1")),
                    Asset("ETH", BigDecimal("100"), BigDecimal("1000")),
                    Asset("BTC", BigDecimal("100"), BigDecimal("10000"))
                )
            )
        )
    }

    fun createPortfolio(portfolio: Portfolio): Boolean {
        return if (portfolios.any { it.name == portfolio.name }) {
            false
        } else {
            portfolios.add(portfolio)
        }
    }

    fun getAllPortfolios(): List<Portfolio> {
        return portfolios
    }

    fun getPortfolioByName(name: String): Portfolio? {
        return portfolios.find { it.name == name }
    }

    fun deletePortfolio(name: String): Boolean {
        return portfolios.removeIf { it.name == name }
    }
}
