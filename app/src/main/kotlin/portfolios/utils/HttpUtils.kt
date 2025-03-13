package portfolios.utils

import org.json.JSONObject
import java.net.URL

class HttpUtils {
    fun makeRequest(url: String): JSONObject {
        val response = URL(url).readText()
        return JSONObject(response)
    }
}
