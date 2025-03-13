package portfolios.utils

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ScaleUtilsTest {
    private val bigDecimal: BigDecimal = BigDecimal(123.4567891251251)
    private val bigDecimalWithTrailingZeroes = BigDecimal(1.0000000000)

    @Test
    fun `setScale correctly scales a BigDecimal to the default scale`() {
        val expected = BigDecimal("123.45678913")
        val result = ScaleUtils.setScale(bigDecimal)

        assertEquals(expected, result)
    }

    @Test
    fun `setScale correctly scales a BigDecimal to a custom scale`() {
        val expected = BigDecimal("123.4568")
        val result = ScaleUtils.setScale(bigDecimal, 4)

        assertEquals(expected, result)
    }

    @Test
    fun `setScale strips trailing zeros`() {
        val expected = BigDecimal("1")
        val result = ScaleUtils.setScale(bigDecimalWithTrailingZeroes)

        assertEquals(expected, result)
    }
}
