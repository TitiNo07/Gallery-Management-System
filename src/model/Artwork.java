package model;

import java.time.LocalDate;
import java.util.Objects;

public class Artwork {
    private String name;
    private Artist author;
    private String genre;
    private LocalDate creationDate;

    public Artwork(String name, Artist author, String genre, LocalDate creationDate) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public Artist getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artwork)) return false;
        Artwork artwork = (Artwork) o;
        return Objects.equals(name, artwork.name) &&
               Objects.equals(author.getName(), artwork.author.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author.getName());
    }

    @Override
    public String toString() {
        return name + " by " + author.getName() + " (" + genre + ", " + creationDate + ")";
    }
}
