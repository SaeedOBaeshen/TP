# Decision Service

## Prerequisites

Before running the service locally, make sure the following are installed:

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

Make sure Docker Desktop is running and configured to use Linux containers.

## Build the Spring Boot JAR

From the project root directory, run:

```bash
mvn clean package
```

This compiles the project, runs the unit tests, and generates:

```text
target/decision-service-0.0.1.jar
```

## Run with Docker

Build the Docker image:

```bash
docker build -t decision-service .
```

Run the container:

```bash
docker run -d --name decision-service -p 8080:8080 decision-service
```

Verify that it is running:

```bash
docker ps
```

View application logs:

```bash
docker logs -f decision-service
```

The service is available at:

```text
http://localhost:8080
```

The decision endpoint is:

```text
POST http://localhost:8080/decision
```

Stop the container:

```bash
docker stop decision-service
```

Start the existing container again:

```bash
docker start decision-service
```

Remove the container:

```bash
docker stop decision-service
docker rm decision-service
```

## Run with Docker Compose

The repository includes a `compose.yaml` file similar to:

```yaml
services:
  decision-service:
    build:
      context: .
      dockerfile: Dockerfile
    image: decision-service
    container_name: decision-service
    ports:
      - "8080:8080"
    restart: unless-stopped
```

Build and start the service:

```bash
docker compose up --build -d
```

Verify that the service is running:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f decision-service
```

Stop and remove the Compose-managed container:

```bash
docker compose down
```

After making application code changes, rebuild the JAR and restart the Docker Compose service:

```bash
mvn clean package
docker compose up --build -d
```

## Troubleshooting

If port `8080` is already in use, stop the application currently using that port or map a different host port:

```bash
docker run -d --name decision-service -p 8081:8080 decision-service
```

The API will then be available at:

```text
http://localhost:8081/decision
```

If Docker reports that the container name already exists, remove the old container:

```bash
docker stop decision-service
docker rm decision-service
```

If Docker cannot connect to `dockerDesktopLinuxEngine`, start Docker Desktop and wait until the Linux Docker engine is running.
