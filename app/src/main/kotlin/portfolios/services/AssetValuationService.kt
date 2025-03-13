package portfolios.services

import java.math.BigDecimal

/**
 * This interface defines the logic for obtaining the current valuation of an asset from one or more APIs.
 */
interface AssetValuationService {
    fun getValuation(): BigDecimal
}
