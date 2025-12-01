# Bajaj SQL Webhook Automation (Spring Boot)

This Spring Boot application implements Bajaj's “SQL Question 2” workflow:

1. **On startup**, it calls `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA` with the required candidate details.
2. The API returns a **webhook URL** and **JWT access token**.
3. The app solves the SQL problem (pre-computed final query) and POSTs the query to the webhook using the provided token.

Everything runs automatically via an `ApplicationRunner`; no REST controllers are exposed.

## Prerequisites

- Java 17+ (tested with Java 20)
- Maven 3.9+ (or use the bundled `mvnw.cmd`/`mvnw`)
- Internet access (to reach the hiring API endpoint)

## Building

```powershell
# From project root
.\mvnw.cmd clean package
```

The runnable JAR will be created at `target\bajajtest-0.0.1-SNAPSHOT.jar`.

## Running

```powershell
java -jar target\bajajtest-0.0.1-SNAPSHOT.jar
```

> Note: `server.port` is set to `8081` in `src/main/resources/application.properties`. Adjust if needed.

## Sample Output

Below is an example startup log showing the complete flow, including a successful webhook submission:

```
2025-12-01T17:05:47.934+05:30  INFO 27520 --- [           main] c.e.bajajtest.BajajtestApplication       : Starting BajajtestApplication using Java 20.0.1
...
2025-12-01T17:05:50.398+05:30  INFO 27520 --- [           main] c.e.b.service.WebhookStartupRunner       : Received webhook URL: https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA
2025-12-01T17:05:50.445+05:30  INFO 27520 --- [           main] c.e.b.service.WebhookStartupRunner       : Submitted finalQuery, webhook response status: 200 OK, body: {"success":true,"message":"Webhook processed successfully"}
```

## Customizing Candidate Details

Update the `GenerateWebhookRequest` instantiation in `WebhookStartupRunner` with your name, registration number, and email before running the solution.


