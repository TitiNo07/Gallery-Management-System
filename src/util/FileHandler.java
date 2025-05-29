package util;

import model.Artwork;
import model.Artist;
import model.ExhibitionManager;
import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

public class FileHandler {
    public static void saveToFile(ExhibitionManager manager, String path) {
    try {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Създава папката ако я няма
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Artist artist : manager.getArtists()) {
                for (Artwork art : artist.getArtworks()) {
                    writer.println(art.getName() + ";" +
                                   artist.getName() + ";" +
                                   artist.getNationality() + ";" +
                                   art.getGenre() + ";" +
                                   art.getCreationDate());
                }
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static void loadFromFile(ExhibitionManager manager, String path) {
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length < 5) continue;

                String artName = parts[0];
                String artistName = parts[1];
                String nationality = parts[2];
                String genre = parts[3];
                LocalDate date = LocalDate.parse(parts[4]);

                Artist artist = manager.getArtistByName(artistName);
                if (artist == null)
                    artist = new Artist(artistName, nationality);

                Artwork artwork = new Artwork(artName, artist, genre, date);
                manager.addArtwork(artwork);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
