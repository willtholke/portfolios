package portfolios

import portfolios.datalayer.Asset
import portfolios.datalayer.Portfolio
import java.math.BigDecimal

/**
 * Contains constants that are used in multiple test classes.
 */
object TestConstants {
    const val DEFAULT_PORTFOLIO_NAME: String = "Will's Portfolio"
    const val NON_EXISTENT_PORTFOLIO_NAME: String = "Elon Musk's Portfolio"
    const val NON_EXISTENT_ASSET_SYMBOL: String = "DOGE"

    val initialAssets = listOf(
        Asset("USD", BigDecimal("10"), BigDecimal("1")),
        Asset("ETH", BigDecimal("10"), BigDecimal("1000")),
        Asset("BTC", BigDecimal("10"), BigDecimal("10000"))
    )

    var mockedPortfolios: List<Portfolio> = listOf(
        Portfolio("Will's Portfolio", initialAssets),
        Portfolio(
            "Ale's Portfolio", listOf(
                Asset("USD", BigDecimal("100"), BigDecimal("1")),
                Asset("ETH", BigDecimal("100"), BigDecimal("1000")),
                Asset("BTC", BigDecimal("100"), BigDecimal("10000"))
            )
        )
    )
}
