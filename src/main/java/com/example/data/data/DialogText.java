package com.example.data.data;

import java.util.Map;

public record DialogText(Map<String, String> dialogTexts) {
    public String getText(String key) {
        return dialogTexts.get(key);
    }
}
