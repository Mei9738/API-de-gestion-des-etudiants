package org.example.project2.config;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.example.project2.exception.CustomFeignClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage;
        String body;

        try {
            if (response.body() != null) {
                body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                errorMessage = extractErrorMessage(body);
            } else {
                errorMessage = "Empty response body";
            }
        } catch (IOException e) {
            errorMessage = "Failed to parse error details";
        }

        String message = String.format("Feign client error for method %s: %d - %s", methodKey, response.status(), errorMessage);
        return new CustomFeignClientException(message, response.status(), "Client error", errorMessage);
    }

    private String extractErrorMessage(String body) {
        List<String> jsonPaths = List.of("$.detail[0].msg", "$.detail", "$.message");

        for (String path : jsonPaths) {
            try {
                Object result = JsonPath.read(body, path);
                return Objects.toString(result, null);
            } catch (PathNotFoundException ignored) {
                // continue to next path
            }
        }

        return body;
    }

}
