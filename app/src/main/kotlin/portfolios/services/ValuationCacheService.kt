package portfolios.services

import org.slf4j.LoggerFactory
import portfolios.utils.ScaleUtils
import java.math.BigDecimal

class ValuationCacheService {
    private val logger = LoggerFactory.getLogger(PortfolioService::class.java)
    private val valuations = mutableMapOf<String, Pair<BigDecimal, Long>>()
    val updateTimeout: Long = 20000L  // 20 seconds

    /**
     * Returns the cached valuation or updates it if the cached valuation is stale.
     */
    fun getValuation(assetSymbol: String, service: AssetValuationService): BigDecimal {
        val (currentValuation, lastUpdated) = valuations[assetSymbol] ?: Pair(BigDecimal.ZERO, Long.MIN_VALUE)

        return if (currentValuation == BigDecimal.ZERO || isUpdateRequired(lastUpdated)) {
            val newValuation = service.getValuation()
            valuations[assetSymbol] = Pair(newValuation, System.currentTimeMillis())
            logger.info("Updated '$assetSymbol' valuation to ${ScaleUtils.setScale(newValuation)} and cached value.")
            newValuation
        } else {
            logger.info(
                "Using cached '$assetSymbol' valuation of ${ScaleUtils.setScale(currentValuation)} until " +
                        "${getCoolDown(lastUpdated)} ms from now."
            )
            currentValuation
        }
    }

    /**
     * Returns the number of milliseconds remaining until the cached valuation can be updated.
     */
    private fun getCoolDown(lastUpdated: Long): Long {
        return updateTimeout - (System.currentTimeMillis() - lastUpdated)
    }

    /**
     * Returns true if the cached valuation can be updated.
     */
    private fun isUpdateRequired(lastUpdated: Long): Boolean {
        return getCoolDown(lastUpdated) <= 0L
    }
}
