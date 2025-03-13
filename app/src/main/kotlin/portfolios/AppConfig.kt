package portfolios

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory
import javax.validation.Valid
import javax.validation.constraints.NotNull

class AppConfig : Configuration() {
    @NotNull
    @Valid
    var appName: String? = null

    @NotNull
    @Valid
    var database = DataSourceFactory()
}
