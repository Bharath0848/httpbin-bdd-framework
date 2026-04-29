# httpbin-bdd-framework

A BDD API test automation framework built with **Java**, **Cucumber**, **REST Assured**, and **TestNG** — targeting the [httpbin.org](https://httpbin.org) API.

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 17 | Core language |
| Maven | — | Build & dependency management |
| Cucumber | 7.14.0 | BDD / Gherkin test authoring |
| REST Assured | 5.4.0 | HTTP client & assertions |
| TestNG | 7.7.1 | Test runner & assertions |
| ExtentReports | 5.0.9 | HTML test reporting |
| Apache POI | 5.2.5 | Excel-driven data tests |
| Jackson | 2.17.2 | JSON serialization (POJO) |
| Log4j2 | 2.23.1 | Logging |

---

## Project Structure

```
httpbin-bdd-framework/
├── src/
│   ├── main/java/com/httpbin/
│   │   ├── endpoints/          # API route constants
│   │   ├── managers/           # ScenarioContext (cross-step state)
│   │   ├── pojo/               # Request/response POJOs
│   │   └── utils/              # ConfigReader, ExcelUtility, RequestBuilder
│   └── test/
│       ├── java/com/httpbin/
│       │   ├── hooks/          # Cucumber Before/After hooks
│       │   ├── runner/         # TestRunner
│       │   └── stepdefinitions/
│       └── resources/
│           ├── features/       # Gherkin feature files
│           ├── schemas/        # JSON schema files
│           └── config.properties
            |__testdata
              ├── Exceldata.xls

└── pom.xml
```

---

## Test Modules

### 1. Anything — CRUD Operations (`/anything`)
Covers the full lifecycle of a resource using the `/anything` echo endpoint.

| Scenario | Method | Technique |
|---|---|---|
| Create a new record | `POST` | Scenario Outline |
| Read record with tracking details | `GET` | DataTable |
| Update full record | `PUT` | Step chaining |
| Partial update (age field) | `PATCH` | Step chaining |
| Delete by saved ID | `DELETE` | Response chaining |
| Invalid path & wrong method | `GET` | Negative testing |

### 2. HTTP Methods (`/get`, `/post`, `/put`, `/patch`, `/delete`)
Validates each HTTP method against its dedicated httpbin endpoint.

- **GET** — query param validation via Scenario Outline
- **POST** — DataTable-driven payload, JSON response verification
- **PUT** — updates field and asserts response
- **PATCH** — invalid input handling
- **DELETE** — query param verification
- All scenarios assert **response time < 3000 ms**

### 3. Dynamic Delay (`/delay/{n}`)
Tests the delay endpoint across a range of inputs.

| Scenario | Details |
|---|---|
| Excel-driven delays | Reads method, seconds, min/max bounds from `Exceldata.xlsx` |
| Decimal & negative values | `3.2`, `2.5`, `-1`, `0` — edge case handling |
| Invalid inputs | `abc`, empty string — expects `404` |
| Wrong path | `/delaay/3` — typo detection |
| Schema validation | POST response validated against `Delayschema.json` |
| Response chaining | PUT response compared against previous POST response |

### 4. Redirect (`/redirect-to`)
Validates redirect behavior without auto-following.

- Bearer token authentication
- Multiple URL redirect via DataTable
- Query parameter combinations via Scenario Outline
- Excel-driven multi-method redirect tests
- Negative: missing param (`500`), empty URL, malformed URL
- Chained redirect → schema validation with `get_response_schema.json`

### 5. Status Codes (`/status/{code}`)
Tests status code handling across all HTTP methods.

| Scenario | Method | Notes |
|---|---|---|
| Single code | `POST` | OAuth2 bearer auth |
| Parametrized code | `GET` | Scenario Outline, basic auth |
| Chained codes | `PUT` | Chains GET result into PUT path |
| No code | `PATCH` | Expects `404` |
| Excel-driven | `DELETE` | Reads code from `Sheet1` |
| Response time | all | Assert < 4000 ms |


## Configuration

Set your credentials and base URL in `src/test/resources/config.properties`:

```properties
base_url=https://httpbin.org
username=user
password=pass
bearer_token=your_token_here
```


## Reports

After a test run, reports are generated in the `target/` directory.

| Report | Path |
|---|---|
| HTML (Cucumber) | `target/cucumber-report.html` |
| JSON | `target/cucumber.json` |
| ExtentReport | `target/extent-report/` |

Live report publishing is enabled via `publish = true` in `TestRunner.java`.

---

## Key Framework Features

- **ScenarioContext** — shares state (e.g., tokens, IDs, headers) between steps within a scenario
- **RequestBuilder** — centralised `RequestSpecification` builder with auth and redirect config
- **ConfigReader** — loads `config.properties` at runtime; no hardcoded credentials
- **ExcelUtility** — reads test data from `.xlsx` sheets using Apache POI
- **JSON Schema Validation** — validates response structure using schema files in `src/test/resources/schemas/`
- **Soft Assertions** — used in Excel-driven redirect tests to collect all failures before reporting
- **Auth Support** — Basic Auth, Bearer Token, OAuth2 via REST Assured built-ins

---

## Authors

**Bharath** 
**ThineshKumaar** 
**Madhan** 
**Mahitha** 
**Arthi** 
