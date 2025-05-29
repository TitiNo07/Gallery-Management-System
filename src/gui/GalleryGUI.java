package gui;

import model.*;
import util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;

public class GalleryGUI extends JFrame {
    private ExhibitionManager manager;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> searchType;

    public GalleryGUI() {
        manager = new ExhibitionManager();
        setTitle("Gallery Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Name", "Author", "Genre", "Date"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        searchType = new JComboBox<>(new String[]{"Author", "Artwork Name"});

        JButton searchBtn = new JButton("Search");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Write to file");
        JButton loadBtn = new JButton("Home");
        JButton openFileBtn = new JButton("Open");

        loadBtn.addActionListener(_ -> {
            FileHandler.loadFromFile(manager, "data/gallery_data.txt");
            refreshTable();
        });
        searchBtn.addActionListener(_ -> search());
        addBtn.addActionListener(_ -> openAddDialog());
        editBtn.addActionListener(_ -> openEditDialog());
        deleteBtn.addActionListener(_ -> deleteSelected());
        saveBtn.addActionListener(_ -> FileHandler.saveToFile(manager, "data/gallery_data.txt"));
        openFileBtn.addActionListener(_ -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        manager = new ExhibitionManager(); // нов обект, за да не се дублира
        FileHandler.loadFromFile(manager, selectedFile.getAbsolutePath());
        refreshTable();
        }
        });

        topPanel.add(loadBtn);
        topPanel.add(searchField);
        topPanel.add(searchType);
        topPanel.add(searchBtn);
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);
        topPanel.add(saveBtn);
        topPanel.add(openFileBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void search() {
        String term = searchField.getText().trim();
        ArrayList<Artwork> results = searchType.getSelectedItem().equals("Author")
                ? manager.searchByAuthor(term)
                : manager.searchByName(term);

        InsertionSorter.sortByDate(results);
        model.setRowCount(0);
        for (Artwork a : results)
            model.addRow(new Object[]{a.getName(), a.getAuthor().getName(), a.getGenre(), a.getCreationDate()});
    }

    private void openAddDialog() {
        JTextField name = new JTextField();
        JTextField author = new JTextField();
        JTextField nationality = new JTextField();
        JTextField genre = new JTextField();
        JTextField date = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Name:")); panel.add(name);
        panel.add(new JLabel("Author:")); panel.add(author);
        panel.add(new JLabel("Nationality:")); panel.add(nationality);
        panel.add(new JLabel("Author:")); panel.add(genre);
        panel.add(new JLabel("Date (YYYY-MM-DD):")); panel.add(date);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add artwork", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Artist a = new Artist(author.getText(), nationality.getText());
                Artwork art = new Artwork(name.getText(), a, genre.getText(), LocalDate.parse(date.getText()));
                if (!manager.addArtwork(art)) {
                    JOptionPane.showMessageDialog(this, "Doubled artwork or artist!");
                }
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data!");
            }
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String artName = model.getValueAt(row, 0).toString();
        String authorName = model.getValueAt(row, 1).toString();

        ArrayList<Artwork> found = manager.searchByAuthor(authorName);
        Artwork art = found.stream().filter(a -> a.getName().equals(artName)).findFirst().orElse(null);
        if (art == null) return;

        JTextField name = new JTextField(art.getName());
        JTextField genre = new JTextField(art.getGenre());
        JTextField date = new JTextField(art.getCreationDate().toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Name:")); panel.add(name);
        panel.add(new JLabel("Genre:")); panel.add(genre);
        panel.add(new JLabel("Date:")); panel.add(date);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            art.setName(name.getText());
            art.setGenre(genre.getText());
            art.setCreationDate(LocalDate.parse(date.getText()));
            refreshTable();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String artName = model.getValueAt(row, 0).toString();
        String authorName = model.getValueAt(row, 1).toString();

        ArrayList<Artwork> found = manager.searchByAuthor(authorName);
        Artwork art = found.stream().filter(a -> a.getName().equals(artName)).findFirst().orElse(null);
        if (art != null) {
            manager.removeArtwork(art);
            refreshTable();
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (ArrayList<Artwork> list : manager.getArtworksByAuthor().values()) {
            for (Artwork a : list)
                model.addRow(new Object[]{a.getName(), a.getAuthor().getName(), a.getGenre(), a.getCreationDate()});
        }
    }
}
