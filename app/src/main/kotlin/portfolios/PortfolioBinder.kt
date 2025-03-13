package portfolios

import org.glassfish.hk2.utilities.binding.AbstractBinder
import portfolios.services.PortfolioService

/*
 * This class configures PortfolioService so that it can be injected into PortfolioResource.
 */
class PortfolioBinder : AbstractBinder() {
    override fun configure() {
        bind(PortfolioService::class.java).to(PortfolioService::class.java)
    }
}
