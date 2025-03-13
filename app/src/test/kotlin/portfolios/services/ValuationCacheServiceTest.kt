package portfolios.services

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ValuationCacheServiceTest {
    @Mock
    private lateinit var mockedValuationService: AssetValuationService

    private lateinit var valuationCacheService: ValuationCacheService

    private val assetSymbol = "BTC"

    @BeforeEach
    fun setUp() {
        valuationCacheService = ValuationCacheService()
    }

    @Test
    fun `getValuation returns new valuation if cache is empty`() {
        val newValuation = BigDecimal("10000")
        `when`(mockedValuationService.getValuation()).thenReturn(newValuation)

        val valuation = valuationCacheService.getValuation(assetSymbol, mockedValuationService)

        assertEquals(newValuation, valuation)
        verify(mockedValuationService).getValuation()
    }

    @Test
    fun `getValuation returns cached valuation if it is not stale`() {
        val cachedValuation = BigDecimal("9000")

        // Call valuation cache once to cache an initial valuation
        `when`(mockedValuationService.getValuation()).thenReturn(cachedValuation)
        valuationCacheService.getValuation(assetSymbol, mockedValuationService)

        // Call valuation cache again in under updateTimeout ms to get the cached valuation
        val valuation = valuationCacheService.getValuation(assetSymbol, mockedValuationService)

        assertEquals(cachedValuation, valuation)
        // Verify that the valuation service was only called once, mitigating extra API calls
        verify(mockedValuationService, times(1)).getValuation()
    }

    @Test
    fun `getValuation updates valuation if cache is stale`() {
        val exceededUpdateTimeout = valuationCacheService.updateTimeout + 1L
        val (staleValuation, newValuation) = Pair(BigDecimal("8000"), BigDecimal("9500"))

        // Call valuation cache once to cache an initial valuation
        `when`(mockedValuationService.getValuation()).thenReturn(staleValuation)
        valuationCacheService.getValuation(assetSymbol, mockedValuationService)

        // Sleep for longer than updateTimeout to make the cached valuation stale
        Thread.sleep(exceededUpdateTimeout)

        `when`(mockedValuationService.getValuation()).thenReturn(newValuation)

        // Call valuation cache again in over updateTimeout ms to get a new valuation
        val valuation = valuationCacheService.getValuation(assetSymbol, mockedValuationService)

        assertEquals(newValuation, valuation)
        // Verify that the valuation service was called twice, since the second call was after updateTimeout ms
        verify(mockedValuationService, times(2)).getValuation()
    }
}
