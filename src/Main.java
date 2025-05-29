import gui.GalleryGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GalleryGUI().setVisible(true);
        });
    }
}
