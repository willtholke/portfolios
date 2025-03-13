package portfolios.datalayer

import portfolios.utils.ScaleUtils
import java.math.BigDecimal

class Asset(
    val symbol: String,
    private var _shares: BigDecimal,
    private var _valuation: BigDecimal,
) {
    var shares: BigDecimal
        get() = ScaleUtils.setScale(_shares)
        set(value) {
            if (value <= BigDecimal.ZERO) {
                throw IllegalArgumentException("Number of shares must be greater than zero.")
            }
            _shares = ScaleUtils.setScale(value)
        }

    var valuation: BigDecimal
        get() = ScaleUtils.setScale(_valuation)
        set(value) {
            if (symbol == "USD" && value != BigDecimal.ONE) {
                throw IllegalArgumentException("Cannot set valuation of asset 'USD'.")
            }
            if (value < BigDecimal.ZERO) {
                throw IllegalArgumentException("Asset valuation must be non-negative.")
            }
            _valuation = ScaleUtils.setScale(value)
        }

    val value: BigDecimal
        get() = ScaleUtils.setScale(_shares * _valuation)

    init {
        if (symbol == "USD") {
            _shares = ScaleUtils.setScale(_shares, 2)
            _valuation = BigDecimal.ONE
        }
    }

    @Override
    override fun toString(): String {
        return """{"symbol": "$symbol", "shares": $_shares, "valuation": $_valuation}"""
    }
}
