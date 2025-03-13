package portfolios.services

import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import portfolios.utils.HttpUtils
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class AssetValuationServiceETHImplTest {
    @Mock
    private lateinit var mockHttpUtils: HttpUtils

    private lateinit var assetValuationService: AssetValuationServiceETHImpl

    private var cryptoCompareUrl: String = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD"
    private var cryptoCompareMockResponse: JSONObject = JSONObject("""{"USD": "20000"}""")

    @BeforeEach
    fun setUp() {
        assetValuationService = AssetValuationServiceETHImpl(mockHttpUtils)
    }

    @Test
    fun `getValuation returns correct valuation from CryptoCompare`() {
        `when`(mockHttpUtils.makeRequest(cryptoCompareUrl)).thenReturn(cryptoCompareMockResponse)

        val valuation = assetValuationService.getValuation()

        assertEquals(BigDecimal("20000"), valuation)
    }

    @Test
    fun `getValuation throws a RuntimeException if CryptoCompare fails`() {
        `when`(mockHttpUtils.makeRequest(cryptoCompareUrl)).thenThrow(RuntimeException(""))

        val exception = assertThrows<RuntimeException> {
            assetValuationService.getValuation()
        }

        assertEquals("Failed to get 'ETH' valuation.", exception.message)
    }

    @Test
    fun `getValuationFromCryptoCompare returns correct valuation`() {
        `when`(mockHttpUtils.makeRequest(cryptoCompareUrl)).thenReturn(cryptoCompareMockResponse)

        val valuation = assetValuationService.getValuationFromCryptoCompare()

        assertEquals(BigDecimal("20000"), valuation)
    }

    @Test
    fun `getValuationFromCryptoCompare handles exception`() {
        `when`(mockHttpUtils.makeRequest(cryptoCompareUrl)).thenThrow(RuntimeException(""))

        val valuation = assetValuationService.getValuationFromCryptoCompare()

        assertNull(valuation)
    }
}
