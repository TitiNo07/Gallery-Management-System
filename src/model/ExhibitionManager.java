package model;

import util.HashMap;
import util.ArrayList;


public class ExhibitionManager {
    private HashMap<String, ArrayList<Artwork>> artworksByGenre;
    private HashMap<String, ArrayList<Artwork>> artworksByAuthor;
    private ArrayList<Artist> artists;

    public ExhibitionManager() {
        artworksByGenre = new HashMap<>();
        artworksByAuthor = new HashMap<>();
        artists = new ArrayList<>();
    }

    public boolean addArtist(Artist artist) {
        if (getArtistByName(artist.getName()) != null) return false;
        artists.add(artist);
        return true;
    }

    public boolean addArtwork(Artwork artwork) {
        ArrayList<Artwork> existing;
        if (artworksByAuthor.containsKey(artwork.getAuthor().getName())) {
            existing = artworksByAuthor.get(artwork.getAuthor().getName());
        } else {
            existing = new ArrayList<>();
        }
        for (Artwork a : existing) {
            if (a.equals(artwork)) return false;
        }

        Artist artist = getArtistByName(artwork.getAuthor().getName());
        if (artist == null) {
            artist = artwork.getAuthor();
            addArtist(artist);
        }
        artist.addArtwork(artwork);

        ArrayList<Artwork> genreList = artworksByGenre.get(artwork.getGenre());
        if (genreList == null) {
            genreList = new ArrayList<>();
            artworksByGenre.put(artwork.getGenre(), genreList);
        }
        genreList.add(artwork);

        ArrayList<Artwork> authorList = artworksByAuthor.get(artist.getName());
        if (authorList == null) {
            authorList = new ArrayList<>();
            artworksByAuthor.put(artist.getName(), authorList);
        }
        authorList.add(artwork);

        return true;
    }

    public boolean removeArtwork(Artwork artwork) {
        String author = artwork.getAuthor().getName();
        String genre = artwork.getGenre();
        ArrayList<Artwork> authorList = artworksByAuthor.containsKey(author) ? artworksByAuthor.get(author) : new ArrayList<>();
        ArrayList<Artwork> genreList = artworksByGenre.containsKey(genre) ? artworksByGenre.get(genre) : new ArrayList<>();
        int authorIndex = authorList.indexOf(artwork);
        int genreIndex = genreList.indexOf(artwork);
        boolean removedFromAuthor = false;
        boolean removedFromGenre = false;
        if (authorIndex != -1) {
            authorList.remove(authorIndex);
            removedFromAuthor = true;
        }
        if (genreIndex != -1) {
            genreList.remove(genreIndex);
            removedFromGenre = true;
        }
        return removedFromAuthor && removedFromGenre;
    }

    public Artist getArtistByName(String name) {
        return artists.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ArrayList<Artwork> searchByAuthor(String author) {
        if (artworksByAuthor.containsKey(author)) {
            return artworksByAuthor.get(author);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<Artwork> searchByName(String name) {
        ArrayList<Artwork> result = new ArrayList<>();
        for (ArrayList<Artwork> artworks : artworksByAuthor.values()) {
            for (Artwork art : artworks) {
                if (art.getName().equalsIgnoreCase(name)) {
                    result.add(art);
                }
            }
        }
        return result;
    }

    public HashMap<String, ArrayList<Artwork>> getArtworksByGenre() {
        return artworksByGenre;
    }

    public HashMap<String, ArrayList<Artwork>> getArtworksByAuthor() {
        return artworksByAuthor;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }
}
