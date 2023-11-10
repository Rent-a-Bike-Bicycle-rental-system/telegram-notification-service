package com.example.data.data;

import java.util.*;

public record DialogTextWithLocalization(Map<String, Map<String, String>> textsWithLocalization) {
    public String getText(String key, String lang) {
        return textsWithLocalization.get(key).get(lang);
    }

    public List<String> getAllTexts(String key) {
        Map<String, String> allTexts = textsWithLocalization.get(key);

        return new ArrayList<>(allTexts.values());
    }
}
