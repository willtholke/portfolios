package portfolios.utils

import java.math.BigDecimal
import java.math.RoundingMode

object ScaleUtils {
    fun setScale(value: BigDecimal, scale: Int = 8): BigDecimal {
        return value.stripTrailingZeros().setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            .toBigDecimal()
    }
}
