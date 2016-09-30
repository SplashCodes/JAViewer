package io.github.javiewer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import io.github.javiewer.adapter.item.Movie;

/**
 * Project: JAViewer
 */

public class Configurations {

    private static File file;

    public ArrayList<Movie> starred_movies = new ArrayList<>();

    public ArrayList<Movie> getStarredMovies() {
        return starred_movies;
    }

    public static Configurations load(File file) {
        try {
            Configurations.file = file;
            return JAViewer.parseJson(Configurations.class, new JsonReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            return new Configurations();
        }
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(this, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
