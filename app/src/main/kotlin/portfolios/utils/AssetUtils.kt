package portfolios.utils

object AssetUtils {
    fun toUpper(assetSymbols: List<String>): List<String> {
        return assetSymbols.map { symbol -> symbol.uppercase() }
    }

    fun hasUniqueAssetSymbols(assetSymbols: List<String>): Boolean {
        val uniqueAssetSymbols = assetSymbols.map { symbol -> symbol }.toSet()
        return uniqueAssetSymbols.size == assetSymbols.size
    }
}
