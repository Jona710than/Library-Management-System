package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OpenLibrarySearch {

    public static class Book {
        public String title;
        public String author;
        public String publisher;
        public String isbn;

        public Book(String title, String author, String publisher, String isbn) {
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.isbn = isbn;
        }

        @Override
        public String toString() {
            return title + " by " + author;
        }
    }

    public static ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> books = new ArrayList<>();

        try {
            keyword = keyword.replace(" ", "+");
            String apiUrl = "https://openlibrary.org/search.json?q=" + keyword;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray docs = json.getAsJsonArray("docs");

                if (docs == null || docs.size() == 0) {
                    System.out.println("No books found for keyword: " + keyword);
                    return books;
                }

                for (int i = 0; i < Math.min(10, docs.size()); i++) {
                    JsonObject doc = docs.get(i).getAsJsonObject();

                    String title = getJsonString(doc, "title");
                    String author = getJsonArrayFirst(doc, "author_name");
                    String publisher = getJsonArrayFirst(doc, "publisher");
                    String isbn = getJsonArrayFirst(doc, "isbn");

                    books.add(new Book(title, author, publisher, isbn));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return books;
    }

    private static String getJsonString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "Unknown";
    }

    private static String getJsonArrayFirst(JsonObject obj, String key) {
        if (obj.has(key)) {
            JsonArray array = obj.getAsJsonArray(key);
            if (array != null && array.size() > 0) {
                JsonElement value = array.get(0);
                if (!value.isJsonNull()) return value.getAsString();
            }
        }
        return "Unknown";
    }
}
