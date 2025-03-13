package portfolios.datalayer

import java.math.BigDecimal
import java.math.RoundingMode

class Portfolio(
    val name: String,
    initialAssets: List<Asset> = listOf()
) {
    private val _assets: MutableList<Asset> = mutableListOf()

    val assets: List<Asset>
        get() = _assets.toList()

    init {
        _assets.addAll(initialAssets)
    }

    fun getTotalValue(): BigDecimal {
        if (_assets.isEmpty()) {
            return BigDecimal.ZERO
        }
        return _assets.sumOf { it.value }
    }

    /**
     * Calculates the percentage of a portfolio's total value represented by an asset with the given $assetSymbol.
     * This method ensures that the sum of the integer percentages of all assets in the portfolio adds up to exactly
     * 100 after rounding to the nearest integer. Any rounding discrepancies are evenly distributed across the assets,
     * following the order in which they appear in the portfolio.
     */
    fun getAssetPercentage(assetSymbol: String): Int {
        val totalValue = getTotalValue()
        if (totalValue <= BigDecimal.ZERO) {
            return 0
        }

        val roundedPercentages = _assets.associate {
            it.symbol to (it.value.divide(totalValue, 4, RoundingMode.HALF_UP) * BigDecimal(100)).toInt()
        }.toMutableMap()

        var discrepancy = 100 - roundedPercentages.values.sum()
        while (discrepancy != 0) {
            for (asset in _assets) {
                if (discrepancy == 0) break
                val percentAdjustment = if (discrepancy > 0) 1 else -1
                // We use !! because we know that the key exists
                roundedPercentages[asset.symbol] = roundedPercentages[asset.symbol]!! + percentAdjustment
                discrepancy -= percentAdjustment
            }
        }

        return roundedPercentages[assetSymbol] ?: 0
    }

    @Override
    override fun toString(): String {
        val assetsJson = _assets.map { asset ->
            val assetJson = asset.toString().removeSuffix("}")
            assetJson + ", \"percentage\": ${getAssetPercentage(asset.symbol)}}"
        }.joinToString(separator = ", ", prefix = "[", postfix = "]")

        return """{"name": "$name", "total_value": ${getTotalValue()}, "assets": $assetsJson}"""
    }
}
