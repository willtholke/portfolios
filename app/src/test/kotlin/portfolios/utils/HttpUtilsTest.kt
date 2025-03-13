package portfolios.utils

import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class HttpUtilsTest {
    @Mock
    private var httpUtils: HttpUtils = HttpUtils()

    @Test
    fun `makeRequest returns correct JSON`() {
        val testUrl = "https://api.com/endpoint"
        val mockResponse = JSONObject("""{"name": "Will"}""")
        `when`(httpUtils.makeRequest(testUrl)).thenReturn(mockResponse)

        val response = httpUtils.makeRequest(testUrl)

        assertTrue(response.has("name"))
        assertEquals(response.getString("name"), "Will")
    }
}
