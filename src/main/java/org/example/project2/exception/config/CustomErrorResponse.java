package org.example.project2.exception.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("status")
    private int status;
    
    @JsonProperty("title")
    private String title;
} 