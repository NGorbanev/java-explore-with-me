package ru.practicum.ewm;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final String SERVICE_NAME = "ewm-main-service";
    public static final String EVENT_URI = "/events/";
    public static final String API_LOGSTRING = "Request received. Endpoint:";
}
