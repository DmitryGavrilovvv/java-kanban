package ru.yandex.javacource.gavrilov.schedule.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.gavrilov.schedule.manager.Type;

import java.io.IOException;

public class TypeTaskAdapter extends TypeAdapter<Type> {
    @Override
    public void write(JsonWriter jsonWriter, Type type) throws IOException {
        jsonWriter.value(type.toString());
    }

    @Override
    public Type read(JsonReader jsonReader) throws IOException {
        return Type.valueOf(jsonReader.nextString());
    }
}
