package com.example.bajajtest.service;

import com.example.bajajtest.dto.FinalQueryRequest;
import com.example.bajajtest.dto.GenerateWebhookRequest;
import com.example.bajajtest.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebhookStartupRunner {

    private static final Logger log = LoggerFactory.getLogger(WebhookStartupRunner.class);

    private static final String GENERATE_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    /**
     * Final SQL query solving the problem:
     *
     * For every department, calculate:
     *  - average age of employees who have any payment > 70000
     *  - concatenated string (up to 10 names) of such employees, formatted "FIRST_NAME LAST_NAME"
     * Ordered by DEPARTMENT_ID DESC.
     */
    private static final String FINAL_SQL_QUERY =
            "WITH high_salary AS (\n" +
            "    SELECT DISTINCT e.EMP_ID,\n" +
            "           e.FIRST_NAME,\n" +
            "           e.LAST_NAME,\n" +
            "           e.DOB,\n" +
            "           e.DEPARTMENT\n" +
            "    FROM EMPLOYEE e\n" +
            "    JOIN PAYMENTS p ON p.EMP_ID = e.EMP_ID\n" +
            "    WHERE p.AMOUNT > 70000\n" +
            "),\n" +
            "high_salary_ranked AS (\n" +
            "    SELECT hs.*,\n" +
            "           ROW_NUMBER() OVER (\n" +
            "               PARTITION BY hs.DEPARTMENT\n" +
            "               ORDER BY hs.FIRST_NAME, hs.LAST_NAME, hs.EMP_ID\n" +
            "           ) AS rn\n" +
            "    FROM high_salary hs\n" +
            "),\n" +
            "dept_avg AS (\n" +
            "    SELECT d.DEPARTMENT_ID,\n" +
            "           d.DEPARTMENT_NAME,\n" +
            "           AVG(TIMESTAMPDIFF(YEAR, hs.DOB, CURDATE())) AS avg_employee_age\n" +
            "    FROM DEPARTMENT d\n" +
            "    LEFT JOIN high_salary hs\n" +
            "           ON d.DEPARTMENT_ID = hs.DEPARTMENT\n" +
            "    GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME\n" +
            ")\n" +
            "SELECT da.DEPARTMENT_ID,\n" +
            "       da.DEPARTMENT_NAME,\n" +
            "       da.avg_employee_age,\n" +
            "       GROUP_CONCAT(CONCAT(hsr.FIRST_NAME, ' ', hsr.LAST_NAME)\n" +
            "                    ORDER BY hsr.FIRST_NAME, hsr.LAST_NAME\n" +
            "                    SEPARATOR ', ') AS employee_names\n" +
            "FROM dept_avg da\n" +
            "LEFT JOIN high_salary_ranked hsr\n" +
            "       ON da.DEPARTMENT_ID = hsr.DEPARTMENT\n" +
            "      AND hsr.rn <= 10\n" +
            "GROUP BY da.DEPARTMENT_ID, da.DEPARTMENT_NAME, da.avg_employee_age\n" +
            "ORDER BY da.DEPARTMENT_ID DESC;";

    @Bean
    public ApplicationRunner applicationRunner(RestTemplate restTemplate) {
        return args -> {
            try {
                // 1. Call generateWebhook endpoint on startup
                GenerateWebhookRequest requestPayload = new GenerateWebhookRequest(
                        "John Doe",
                        "REG12347",
                        "john@example.com"
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<GenerateWebhookRequest> requestEntity =
                        new HttpEntity<>(requestPayload, headers);

                ResponseEntity<GenerateWebhookResponse> responseEntity =
                        restTemplate.postForEntity(
                                GENERATE_WEBHOOK_URL,
                                requestEntity,
                                GenerateWebhookResponse.class
                        );

                GenerateWebhookResponse responseBody = responseEntity.getBody();
                if (responseBody == null ||
                        responseBody.getWebhook() == null ||
                        responseBody.getAccessToken() == null) {
                    log.error("generateWebhook response missing required fields: {}", responseBody);
                    return;
                }

                String webhookUrl = responseBody.getWebhook();
                String accessToken = responseBody.getAccessToken();

                log.info("Received webhook URL: {}", webhookUrl);

                // 2. Build final query payload
                FinalQueryRequest finalQueryRequest = new FinalQueryRequest(FINAL_SQL_QUERY);

                HttpHeaders submitHeaders = new HttpHeaders();
                submitHeaders.setContentType(MediaType.APPLICATION_JSON);
                // As per problem statement: Authorization: <accessToken> (no Bearer prefix)
                submitHeaders.set("Authorization", accessToken);

                HttpEntity<FinalQueryRequest> finalQueryEntity =
                        new HttpEntity<>(finalQueryRequest, submitHeaders);

                // 3. Submit the solution to the webhook URL
                ResponseEntity<String> submitResponse = restTemplate.postForEntity(
                        webhookUrl,
                        finalQueryEntity,
                        String.class
                );

                log.info("Submitted finalQuery, webhook response status: {}, body: {}",
                        submitResponse.getStatusCode(), submitResponse.getBody());
            } catch (Exception ex) {
                log.error("Error during webhook workflow execution", ex);
            }
        };
    }
}



