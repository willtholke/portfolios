package portfolios.controllers

import portfolios.datalayer.Portfolio
import portfolios.exceptions.AssetNotFoundException
import portfolios.exceptions.PortfolioNotFoundException
import portfolios.exceptions.ResourceAlreadyExistsException
import portfolios.services.PortfolioService
import portfolios.utils.AssetUtils
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/portfolio")
@Produces(MediaType.APPLICATION_JSON)
class PortfolioResource(
    private val portfolioService: PortfolioService = PortfolioService(),
) {
    /**
     * Creates a new portfolio with the given name and an empty list of assets.
     */
    @POST
    @Path("/create/{name}")
    fun createEmptyPortfolio(
        @PathParam("name") portfolioName: String,
    ): Response {
        return try {
            portfolioService.createPortfolio(portfolioName)
            val httpEntity = "Created portfolio '$portfolioName'."
            Response.status(Response.Status.CREATED).entity(httpEntity).build()
        } catch (e: ResourceAlreadyExistsException) {
            Response.status(Response.Status.CONFLICT).entity(e.message).build()
        }
    }

    /**
     * Gets all portfolios, filtering by a list of asset symbols if provided.
     */
    @GET
    @Path("/")
    fun getAllPortfolios(
        @QueryParam("assets") assetSymbols: List<String> = emptyList()
    ): Response {
        var portfolios = portfolioService.getAllPortfolios()
        if (assetSymbols.isNotEmpty()) {
            try {
                portfolios = portfolioService.filterPortfoliosByAsset(portfolios, AssetUtils.toUpper(assetSymbols))
            } catch (e: AssetNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.message).build()
            } catch (e: BadRequestException) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
            }
        }
        val httpEntity = portfolios.toString()
        return Response.status(Response.Status.OK).entity(httpEntity).build()
    }

    /**
     * Gets a portfolio by name, filtering by a list of asset symbols if provided.
     */
    @GET
    @Path("/{name}")
    fun getPortfolioByName(
        @PathParam("name") portfolioName: String,
        @QueryParam("assets") assetSymbols: List<String> = emptyList()
    ): Response {
        return try {
            val portfolio = portfolioService.getPortfolioByName(portfolioName)
            if (assetSymbols.isNotEmpty()) {
                return try {
                    val filteredPortfolio =
                        portfolioService.filterPortfolioByAsset(portfolio, AssetUtils.toUpper(assetSymbols))
                    Response.status(Response.Status.OK).entity(filteredPortfolio.toString()).build()
                } catch (e: AssetNotFoundException) {
                    Response.status(Response.Status.NOT_FOUND).entity(e.message).build()
                } catch (e: BadRequestException) {
                    Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
                }
            }
            val httpEntity = portfolio.toString()
            Response.status(Response.Status.OK).entity(httpEntity).build()
        } catch (e: PortfolioNotFoundException) {
            Response.status(Response.Status.NOT_FOUND).entity(e.message).build()
        }
    }

    /**
     * Deletes a portfolio by name.
     */
    @DELETE
    @Path("/{name}")
    fun deletePortfolio(
        @PathParam("name") portfolioName: String,
    ): Response {
        return try {
            portfolioService.deletePortfolio(portfolioName)
            Response.status(Response.Status.OK).entity("Deleted portfolio '$portfolioName'.").build()
        } catch (e: PortfolioNotFoundException) {
            Response.status(Response.Status.NOT_FOUND).entity(e.message).build()
        }
    }

    /**
     * Gets the total value of a portfolio by name.
     */
    @GET
    @Path("/{name}/total-value")
    fun getTotalValue(
        @PathParam("name") portfolioName: String
    ): Response {
        val portfolio: Portfolio
        return try {
            portfolio = portfolioService.getPortfolioByName(portfolioName)
            val httpEntity = portfolio.getTotalValue()
            Response.status(Response.Status.OK).entity(httpEntity).build()
        } catch (e: PortfolioNotFoundException) {
            Response.status(Response.Status.NOT_FOUND).entity(e.message).build()
        }
    }
}
