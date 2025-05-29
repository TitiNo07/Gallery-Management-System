package model;

import util.ArrayList;

public class Artist {
    private String name;
    private String nationality;
    private ArrayList<Artwork> artworks;

    public Artist(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
        this.artworks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public ArrayList<Artwork> getArtworks() {
        return artworks;
    }

    public void addArtwork(Artwork artwork) {
        artworks.add(artwork);
    }

    @Override
    public String toString() {
        return name + " (" + nationality + ")";
    }
}
