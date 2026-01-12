package com.nutricheck.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class AiRequest {

    private String message = "How are you? How can you help me";
}
