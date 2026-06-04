package ru.home_bookkeeping.backend;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Класс нужен для корректной записи даты в json, тк у Gson нет доступа к приватным полям LocalDate
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        out.value(date.format(formatter));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String str = in.nextString();
        LocalDate date = LocalDate.parse(str, formatter);
        return date;
    }
}