package portfolios.services

import portfolios.App
import portfolios.utils.HttpUtils
import java.math.BigDecimal
import java.math.MathContext
import java.util.logging.Logger

class AssetValuationServiceBTCImpl(
    private val httpUtils: HttpUtils = HttpUtils()
) : AssetValuationService {
    private val logger = Logger.getLogger(App::class.java.name)

    /**
     * Obtains the current valuation of BTC from Coinbase, or CoinDesk if Coinbase fails.
     */
    override fun getValuation(): BigDecimal {
        return getValuationFromCoinbase() ?: getValuationFromCoinDesk() ?: throw RuntimeException(
            "Failed to get 'BTC' valuation."
        )
    }

    fun getValuationFromCoinbase(): BigDecimal? {
        return try {
            logger.info("Obtained 'BTC' valuation from Coinbase.")
            val jsonRequest = httpUtils.makeRequest("https://api.coinbase.com/v2/exchange-rates")
            val exchangeRate = jsonRequest.getJSONObject("data").getJSONObject("rates").getBigDecimal("BTC")
            BigDecimal.ONE.divide(exchangeRate, MathContext.DECIMAL64)  // BTC to USD
        } catch (e: Exception) {
            logger.info("Failed to get 'BTC' valuation from Coinbase: ${e.message}.")
            null
        }
    }

    fun getValuationFromCoinDesk(): BigDecimal? {
        return try {
            logger.info("Obtained 'BTC' valuation from CoinDesk.")
            val jsonRequest = httpUtils.makeRequest("https://api.coindesk.com/v1/bpi/currentprice.json")
            jsonRequest.getJSONObject("bpi").getJSONObject("USD").getBigDecimal("rate_float")
        } catch (e: Exception) {
            logger.warning("Failed to get 'BTC' valuation from CoinDesk: ${e.message}.")
            null
        }
    }
}
