package util;

import model.Artwork;

public class InsertionSorter {
    public static void sortByDate(ArrayList<Artwork> artworks) {
        for (int i = 1; i < artworks.size(); i++) {
            Artwork key = artworks.get(i);
            int j = i - 1;

            while (j >= 0 && artworks.get(j).getCreationDate().isAfter(key.getCreationDate())) {
                artworks.set(j + 1, artworks.get(j));
                j--;
            }

            artworks.set(j + 1, key);
        }
    }
}
