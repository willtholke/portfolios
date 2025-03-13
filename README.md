# Portfolios API

## API Endpoints

Endpoints are configured in [controllers/PortfolioResource](app/src/main/kotlin/portfolios/controllers/PortfolioResource.kt):

1. `POST /api/portfolios/create/{name}`
   - Create a new portfolio with the given name and an empty list of assets
2. `GET /api/portfolios`
   - Get all portfolios
   - Query param `assets` can be provided to filter by a list of asset symbols
4. `GET /api/portfolios/{name}`
   - Get a portfolio by name
   - Query param `assets` can be provided to filter by a list of asset symbols
5. `DELETE /api/portfolios/{name}`
   - Delete a portfolio by name
6. `GET /api/portfolios/{name}/total-value`
   - Get the total value of a portfolio by name

## Examples

Start the application:

```bash
./gradlew clean run --args='server config/app.yml'
```

### Get all portfolios

```bash
curl -X GET "http://localhost:8080/api/portfolio/"
```

You should see "Will's Portfolio" and "Ale's Portfolio". 
The values of BTC and ETH will be cached for 20 seconds after the request is made.

### Get all portfolios with only BTC and ETH assets

```bash
curl -X GET "http://localhost:8080/api/portfolio?assets=BTC&assets=ETH"
```

### Get "Will's Portfolio" with only the USD asset

```bash
curl -X GET "http://localhost:8080/api/portfolio/Will%27s%20Portfolio?assets=USD"
```

### Delete "Will's Portfolio"

```bash
curl -X DELETE "http://localhost:8080/api/portfolio/Will%27s%20Portfolio"
```

### Get the total value of "Will's Portfolio"

```bash
curl -X GET "http://localhost:8080/api/portfolio/Will%27s%20Portfolio/total-value"
```

You should get a 404 with the message "Portfolio 'Will's Portfolio' not found."

### Create an empty portfolio "Empty Portfolio"

```bash
curl -X POST "http://localhost:8080/api/portfolio/create/Empty%20Portfolio"
```

### Get "Empty Portfolio" with only the BTC asset

```bash
curl -X GET "http://localhost:8080/api/portfolio/Empty%20Portfolio?assets=BTC"
```

You should get a 404 with the message "Asset 'BTC' not found in portfolio 'Empty Portfolio'."

### Get the total value of "Empty Portfolio"

```bash
curl -X GET "http://localhost:8080/api/portfolio/Empty%20Portfolio/total-value"
```

Since the portfolio is empty, the total value should be returned as 0.

## Testing

Run all unit tests located in [test/kotlin/portfolios](app/src/test/kotlin/portfolios):


```bash
./gradlew clean test
```