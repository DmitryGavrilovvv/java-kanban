package ru.yandex.javacource.gavrilov.schedule.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (Objects.isNull(localDateTime)) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(localDateTime.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        LocalDateTime time;
        try {
            time = LocalDateTime.parse(jsonReader.nextString(), dtf);
        } catch (DateTimeParseException ex) {
            time = null;
        }
        return time;
    }
}
