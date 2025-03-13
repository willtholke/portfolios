package portfolios.services

import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import portfolios.utils.HttpUtils
import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class AssetValuationServiceBTCImplTest {
    @Mock
    private lateinit var mockHttpUtils: HttpUtils

    private lateinit var assetValuationService: AssetValuationServiceBTCImpl

    private var coinbaseUrl: String = "https://api.coinbase.com/v2/exchange-rates"
    private var coinDeskUrl: String = "https://api.coindesk.com/v1/bpi/currentprice.json"
    private var coinbaseMockResponse: JSONObject = JSONObject("""{"data":{"rates":{"BTC": "20000"}}}""")
    private var coinDeskMockResponse: JSONObject = JSONObject("""{"bpi":{"USD":{"rate_float": "20000"}}}""")

    @BeforeEach
    fun setUp() {
        assetValuationService = AssetValuationServiceBTCImpl(mockHttpUtils)
    }

    @Test
    fun `getValuation returns correct valuation from Coinbase`() {
        `when`(mockHttpUtils.makeRequest(coinbaseUrl)).thenReturn(coinbaseMockResponse)

        val valuation = assetValuationService.getValuation()

        assertEquals(BigDecimal.ONE.divide(BigDecimal("20000"), MathContext.DECIMAL64), valuation)
    }

    @Test
    fun `getValuation returns correct valuation from CoinDesk when Coinbase fails`() {
        `when`(mockHttpUtils.makeRequest(coinbaseUrl)).thenThrow(RuntimeException(""))
        `when`(mockHttpUtils.makeRequest(coinDeskUrl)).thenReturn(coinDeskMockResponse)

        val valuation = assetValuationService.getValuation()

        verify(mockHttpUtils).makeRequest(coinbaseUrl)
        verify(mockHttpUtils).makeRequest(coinDeskUrl)
        assertEquals(BigDecimal("20000"), valuation)
    }

    @Test
    fun `getValuation throws a RuntimeException if both Coinbase and CoinDesk fail`() {
        `when`(mockHttpUtils.makeRequest(coinbaseUrl)).thenThrow(RuntimeException(""))
        `when`(mockHttpUtils.makeRequest(coinDeskUrl)).thenThrow(RuntimeException(""))

        val exception = assertThrows<RuntimeException> {
            assetValuationService.getValuation()
        }

        verify(mockHttpUtils).makeRequest(coinbaseUrl)
        verify(mockHttpUtils).makeRequest(coinDeskUrl)
        assertEquals("Failed to get 'BTC' valuation.", exception.message)
    }

    @Test
    fun `getValuationFromCoinbase returns correct valuation`() {
        `when`(mockHttpUtils.makeRequest(coinbaseUrl)).thenReturn(coinbaseMockResponse)

        val valuation = assetValuationService.getValuationFromCoinbase()

        assertEquals(BigDecimal.ONE.divide(BigDecimal("20000"), MathContext.DECIMAL64), valuation)
    }

    @Test
    fun `getValuationFromCoinbase handles exception`() {
        `when`(mockHttpUtils.makeRequest(coinbaseUrl)).thenThrow(RuntimeException(""))

        val valuation = assetValuationService.getValuationFromCoinbase()

        assertNull(valuation)
    }

    @Test
    fun `getValuationFromCoinDesk returns correct valuation`() {
        `when`(mockHttpUtils.makeRequest(coinDeskUrl)).thenReturn(coinDeskMockResponse)

        val valuation = assetValuationService.getValuationFromCoinDesk()

        assertEquals(BigDecimal("20000"), valuation)
    }

    @Test
    fun `getValuationFromCoinDesk handles exception`() {
        `when`(mockHttpUtils.makeRequest(coinDeskUrl)).thenThrow(RuntimeException(""))

        val valuation = assetValuationService.getValuationFromCoinDesk()

        assertNull(valuation)
    }
}
