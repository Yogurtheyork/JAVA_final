package GoogleCalendar.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class TestJsonUnit {
    public static void main(String[] args) throws IOException {
        JsonReader jsonReader = new JsonReader(new FileReader("src/main/resources/events.json"));


    }
}
