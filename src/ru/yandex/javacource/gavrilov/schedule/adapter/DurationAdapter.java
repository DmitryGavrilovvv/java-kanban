package ru.yandex.javacource.gavrilov.schedule.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration == null) {
            out.value("PT0M");
            return;
        }
        out.value(duration.toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String str = jsonReader.nextString();
        if (str.equals("PT0M")) {
            return null;
        }
        return Duration.parse(str);
    }
}
