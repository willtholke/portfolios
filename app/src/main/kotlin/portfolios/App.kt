package portfolios

import io.dropwizard.Application
import io.dropwizard.setup.Environment
import portfolios.controllers.PortfolioResource
import java.util.logging.Logger

class App : Application<AppConfig>() {
    private val logger = Logger.getLogger(App::class.java.name)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            App().run(*args)
        }
    }

    override fun run(configuration: AppConfig?, environment: Environment?) {
        require(configuration != null && environment != null)

        // register jersey resources
        environment.jersey().register(PortfolioResource())
        environment.jersey().register(PortfolioBinder())

        logger.info("application ${configuration.appName} running")
    }
}
