package com.example.config;

import com.example.data.data.DialogText;
import com.example.data.data.DialogTextWithLocalization;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Configuration
public class DialogConfig {
    @Value("${json.onlyText}")
    private String dialogPathJson;

    @Value("${json.localization}")
    private String dialogWithLocalizationPathJson;

    @Bean
    public DialogText getDialogTexts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        Map<String, String> map = mapper.readValue(new File("./" + dialogPathJson), typeRef);
        return new DialogText(map);
    }

    @Bean
    public DialogTextWithLocalization getDialogTextsWithLocalization() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Map<String, String>>> typeRef = new TypeReference<>() {};
        Map<String, Map<String, String>> map = mapper.readValue(new File("./" + dialogWithLocalizationPathJson), typeRef);
        return new DialogTextWithLocalization(map);
    }
}
