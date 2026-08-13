# Decision Service

This service evaluates merchant purchase transactions and returns a financing decision including the approved amount, interest rate, repayment terms, and the reason for the decision.

## Prerequisites

Install the following before running the service:

* Java 25
* Maven
* Docker Desktop
* Docker Compose

Verify the installations:

```bash
java -version
mvn -version
docker --version
docker compose version
```

## Build the Application

From the project root directory, run:

```bash
mvn clean package
```

This compiles the application, runs the unit tests, and creates the Spring Boot JAR under:

```text
target/
```

For example:

```text
target/tradepay-0.0.1-SNAPSHOT.jar
```

## Run Unit Tests

To run the tests independently:

```bash
mvn test
```

A successful test run should end with:

```text
BUILD SUCCESS
```

## Build the Docker Image

Make sure Docker Desktop is running and using Linux containers.

From the project root, run:

```bash
docker build -t tradepay-decision-service .
```

Verify the image was created:

```bash
docker images
```

You should see:

```text
tradepay-decision-service
```

## Run with Docker

Start the service:

```bash
docker run -d --name tradepay-decision -p 8080:8080 tradepay-decision-service
```

The API will be available at:

```text
http://localhost:8080
```

### View Logs

```bash
docker logs -f tradepay-decision
```

### Stop the Container

```bash
docker stop tradepay-decision
```

### Start the Existing Container Again

```bash
docker start tradepay-decision
```

### Remove the Container

```bash
docker rm tradepay-decision
```

## Run with Docker Compose

Create a `compose.yaml` file in the project root:

```yaml
services:
  tradepay-decision:
    build:
      context: .
      dockerfile: Dockerfile
    image: tradepay-decision-service
    container_name: tradepay-decision
    ports:
      - "8080:8080"
    restart: unless-stopped
```

Build and start the service:

```bash
docker compose up --build -d
```

Check that it is running:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f
```

Stop the service:

```bash
docker compose down
```

To rebuild after code changes:

```bash
mvn clean package
docker compose up --build -d
```

## Test the Decision API

Send a POST request to:

```text
POST http://localhost:8080/decision
```

Use this header:

```text
Content-Type: application/json
```

Example request:

```json
{
  "merchant_id": "M1021",
  "merchant_business_description": "A small grocery store in a residential area of Riyadh, operating for 5 years.",
  "risk_tier": "B",
  "credit_limit": 50000,
  "current_exposure": 30000,
  "transaction_amount": 12000,
  "monthly_purchase_volume": 65000,
  "inventory_level": {
    "sku_A": 100,
    "sku_B": 50,
    "sku_C": 200
  }
}
```

Example response:

```json
{
  "decision": "APPROVED",
  "approved_amount": 12000,
  "interest_rate": 1.75,
  "repayment_terms": "60 days",
  "reason": "The transaction satisfies the merchant's credit and risk requirements."
}
```

A partial approval may look like:

```json
{
  "decision": "PARTIALLY_APPROVED",
  "approved_amount": 8000,
  "interest_rate": 1.5,
  "repayment_terms": "30 days",
  "reason": "The transaction amount exceeds the remaining credit limit. The approved amount is based on the available credit."
}
```

## Troubleshooting

If Docker reports that port `8080` is already in use, stop any locally running Spring Boot application or use another host port:

```bash
docker run -d --name tradepay-decision -p 8081:8080 tradepay-decision-service
```

Then access the API at:

```text
http://localhost:8081/decision
```

If Docker reports that the container name already exists:

```bash
docker rm tradepay-decision
```

Then run the container again.

If Docker reports that it cannot connect to `dockerDesktopLinuxEngine`, start Docker Desktop and wait until the Linux Docker engine is running.

## Dockerfile

The project uses a Linux-based Java 25 runtime image:

```dockerfile
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

The Spring Boot JAR is built on the host using Maven and then copied into the Linux container image.

