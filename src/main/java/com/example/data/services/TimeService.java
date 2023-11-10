package com.example.data.services;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {
    public String formatTimestampToString(long timestamp) {
        var instant = Instant.ofEpochMilli(timestamp);
        var dateTime = instant.atZone(ZoneId.systemDefault());
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        return dateTime.format(formatter);
    }
}
