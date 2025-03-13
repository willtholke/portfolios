package portfolios.controllers

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class HealthResponse(val status: String)

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
class SampleResource {
    @GET
    fun checkHealth(): HealthResponse {
        return HealthResponse("System operational!")
    }
}
