package portfolios.services

import org.slf4j.LoggerFactory
import portfolios.datalayer.Portfolio
import portfolios.datalayer.mockdb.MockDatabase
import portfolios.exceptions.AssetNotFoundException
import portfolios.exceptions.PortfolioNotFoundException
import portfolios.exceptions.ResourceAlreadyExistsException
import portfolios.utils.AssetUtils
import javax.ws.rs.BadRequestException

class PortfolioService(
    private val valuationServiceBTC: AssetValuationServiceBTCImpl = AssetValuationServiceBTCImpl(),
    private val valuationServiceETH: AssetValuationServiceETHImpl = AssetValuationServiceETHImpl(),
    private val valuationCacheService: ValuationCacheService = ValuationCacheService(),
    private val mockDatabase: MockDatabase = MockDatabase()
) {
    private val logger = LoggerFactory.getLogger(PortfolioService::class.java)

    /**
     * Creates a new portfolio with the given name and an empty list of assets if it does not already exist.
     */
    fun createPortfolio(portfolioName: String) {
        val portfolio = Portfolio(portfolioName, emptyList())
        if (!mockDatabase.createPortfolio(portfolio)) {
            logger.info("Could not create portfolio '${portfolio.name}'.")
            throw ResourceAlreadyExistsException("Portfolio '${portfolio.name}' already exists.")
        }
        logger.info("Created portfolio '${portfolio.name}'.")
    }

    /**
     * Gets all portfolios and updates their asset valuations.
     */
    fun getAllPortfolios(): List<Portfolio> {
        val portfolios = mockDatabase.getAllPortfolios()

        portfolios.forEach { portfolio -> updateAssetValuations(portfolio) }
        return portfolios
    }

    /**
     * Gets a portfolio by name and updates its asset valuations if it exists.
     */
    fun getPortfolioByName(name: String): Portfolio {
        val portfolio = mockDatabase.getPortfolioByName(name)
            ?: throw PortfolioNotFoundException("Portfolio '$name' not found.")

        updateAssetValuations(portfolio)
        return portfolio
    }

    /**
     * Deletes a portfolio by name if it exists.
     */
    fun deletePortfolio(name: String) {
        if (!mockDatabase.deletePortfolio(name)) {
            logger.info("Could not delete portfolio '$name'.")
            throw PortfolioNotFoundException("Portfolio '$name' not found.")
        }
        logger.info("Deleted portfolio '$name'.")
    }

    /**
     * Filters an existing portfolio in place by a list of asset symbols that:
     * 1) do not contain duplicates
     * 2) are not empty
     * 3) do not contain invalid characters, and
     * 4) are present in the portfolio.
     */
    fun filterPortfolioByAsset(portfolio: Portfolio, assetSymbols: List<String>): Portfolio {
        logger.info("Filtering portfolio ${portfolio.name} for assets: $assetSymbols.")
        val assetsMap = portfolio.assets.associateBy { it.symbol }

        if (!AssetUtils.hasUniqueAssetSymbols(assetSymbols)) {
            throw BadRequestException("Cannot filter portfolio by duplicate asset symbols.")
        }

        val filteredAssets = assetSymbols.map { symbol ->
            if (symbol.isEmpty()) {
                throw BadRequestException("Asset symbol cannot be empty.")
            }
            if (!symbol.matches(Regex("^[a-zA-Z]+$"))) {
                throw BadRequestException("Asset symbol '$symbol' contains invalid characters.")
            }
            assetsMap[symbol]
                ?: throw AssetNotFoundException("Asset '$symbol' not found in portfolio '${portfolio.name}'.")
        }

        val updatedPortfolio = Portfolio(portfolio.name, filteredAssets)
        updateAssetValuations(updatedPortfolio)

        return Portfolio(portfolio.name, filteredAssets)
    }

    /**
     * Filters a list of existing portfolios in place by a list of asset symbols.
     */
    fun filterPortfoliosByAsset(portfolios: List<Portfolio>, assetSymbols: List<String>): List<Portfolio> {
        return portfolios.map { portfolio -> filterPortfolioByAsset(portfolio, assetSymbols) }
    }

    /**
     * Updates the valuation of non-USD assets in a given portfolio, using the valuation cache service
     * to retrieve the most recent valuations.
     */
    fun updateAssetValuations(portfolio: Portfolio) {
        portfolio.assets.forEach { asset ->
            when (asset.symbol) {
                "BTC" -> {
                    logger.info("Updating 'BTC' valuation in portfolio '${portfolio.name}'.")
                    asset.valuation = (valuationCacheService.getValuation(asset.symbol, valuationServiceBTC))
                }
                "ETH" -> {
                    logger.info("Updating 'ETH' valuation in portfolio '${portfolio.name}'.")
                    asset.valuation = (valuationCacheService.getValuation(asset.symbol, valuationServiceETH))
                }
                else -> logger.info("No update required for asset '${asset.symbol}'.")
            }
        }
    }
}
