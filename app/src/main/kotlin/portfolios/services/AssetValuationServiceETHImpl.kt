package portfolios.services

import portfolios.App
import portfolios.utils.HttpUtils
import java.math.BigDecimal
import java.util.logging.Logger

class AssetValuationServiceETHImpl(
    private val httpUtils: HttpUtils = HttpUtils()
) : AssetValuationService {
    private val logger = Logger.getLogger(App::class.java.name)

    /**
     * Obtains the current valuation of ETH from CryptoCompare.
     */
    override fun getValuation(): BigDecimal {
        return getValuationFromCryptoCompare() ?: throw RuntimeException(
            "Failed to get 'ETH' valuation."
        )
    }

    fun getValuationFromCryptoCompare(): BigDecimal? {
        return try {
            logger.info("Obtained 'ETH' valuation from CryptoCompare.")
            val jsonRequest = httpUtils.makeRequest("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD")
            return jsonRequest.getBigDecimal("USD")
        } catch (e: Exception) {
            logger.info("Failed to get 'ETH' valuation from CryptoCompare: ${e.message}.")
            null
        }
    }
}
