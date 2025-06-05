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
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Name", "Author", "Genre", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 1;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.setBackground(new Color(255,255,255));
        searchField = new JTextField(20);
        searchType = new JComboBox<>(new String[]{"Author", "Artwork Name"});
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(searchBtn);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(255,255,255));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Home");
        JButton openFileBtn = new JButton("Open");
        topPanel.add(loadBtn);
        topPanel.add(searchPanel);
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);
        topPanel.add(saveBtn);
        topPanel.add(openFileBtn);
        add(topPanel, BorderLayout.NORTH);

        Color background = new Color(255, 255 , 255); 
        Color panelColor = new Color(255, 255, 255); 
        Color buttonColor = new Color(242, 112, 0); 
        Color buttonText = Color.BLACK;
        Color tableHeader = new Color(3, 156, 221);
        Color tableRow = new Color(240, 248, 255); 

        getContentPane().setBackground(background);
        topPanel.setBackground(panelColor);
        table.setBackground(tableRow);
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionBackground(new Color(0, 196, 222));
        table.getTableHeader().setBackground(tableHeader);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        JButton[] buttons = {searchBtn, addBtn, editBtn, deleteBtn, saveBtn, loadBtn, openFileBtn};
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(buttonText);
        }

        String[] genres = {"Painting", "Sculpture", "Photography", "Drawing", "Other"};

        addBtn.addActionListener(_ -> openAddDialog(genres));
        editBtn.addActionListener(_ -> openEditDialog(genres));

        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && col >= 0) {
                    String name = (String) model.getValueAt(row, 0);
                    String author = (String) model.getValueAt(row, 1);
                    String genre = (String) model.getValueAt(row, 2);
                    String dateStr = (String) model.getValueAt(row, 3);
                    if (name.isEmpty() || author.isEmpty() || genre.isEmpty() || dateStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "All fields required.");
                        refreshTable();
                        return;
                    }
                    boolean validGenre = false;
                    for (String g : genres) if (g.equalsIgnoreCase(genre)) validGenre = true;
                    if (!validGenre && genre.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Invalid genre.");
                        refreshTable();
                        return;
                    }
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    try {
                        LocalDate.parse(dateStr, formatter);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use DD-MM-YYYY.");
                        refreshTable();
                        return;
                    }
                    java.util.List<Artwork> found = manager.searchByName(name).stream().filter(a -> a.getAuthor().getName().equals(author)).toList();
                    if (!found.isEmpty()) {
                        Artwork art = found.get(0);
                        art.setName(name);
                        art.setGenre(genre);
                        art.setCreationDate(LocalDate.parse(dateStr, formatter));
                    }
                }
            }
        });

        loadBtn.addActionListener(_ -> {
            FileHandler.loadFromFile(manager, "data/gallery_data.txt");
            refreshTable();
        });
        searchBtn.addActionListener(_ -> search());
        deleteBtn.addActionListener(_ -> deleteSelected());
        saveBtn.addActionListener(_ -> FileHandler.saveToFile(manager, "data/gallery_data.txt"));
        openFileBtn.addActionListener(_ -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        manager = new ExhibitionManager();
        FileHandler.loadFromFile(manager, selectedFile.getAbsolutePath());
        refreshTable();
        }
        });
    }

    private void search() {
        String term = searchField.getText().trim();
        ArrayList<Artwork> results;
        if ("Author".equals(searchType.getSelectedItem())) {
            results = manager.searchByAuthor(term);
        } else {
            results = manager.searchByName(term);
        }

        InsertionSorter.sortByDate(results);
        model.setRowCount(0);
        for (Artwork a : results)
            model.addRow(new Object[]{a.getName(), a.getAuthor().getName(), a.getGenre(), a.getCreationDate()});
    }

    private void openAddDialog(String[] genres) {
        JTextField nameField = new JTextField();
        JTextField authorField = new JTextField();
        JComboBox<String> genreBox = new JComboBox<>(genres);
        JTextField customGenreField = new JTextField();
        customGenreField.setVisible(false);
        JTextField dateField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Artwork Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreBox);
        panel.add(customGenreField);
        panel.add(new JLabel("Date (DD-MM-YYYY):"));
        panel.add(dateField);

        genreBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boolean isOther = genreBox.getSelectedItem().equals("Other");
                customGenreField.setVisible(isOther);
                panel.revalidate();
                panel.repaint();
            }
        });
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Artwork", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String author = authorField.getText().trim();
            String genre = (String) genreBox.getSelectedItem();
            if (genre.equals("Other")) {
                genre = customGenreField.getText().trim();
            }
            String dateStr = dateField.getText().trim();
            if (name.isEmpty() || author.isEmpty() || genre.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                Artist artist = manager.getArtistByName(author);
                if (artist == null) artist = new Artist(author, "");
                Artwork art = new Artwork(name, artist, genre, date);
                if (!manager.addArtwork(art)) {
                    JOptionPane.showMessageDialog(this, "Artwork already exists.");
                } else {
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use DD-MM-YYYY.");
            }
        }
    }

    private void openEditDialog(String[] genres) {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String name = (String) model.getValueAt(row, 0);
        String author = (String) model.getValueAt(row, 1);
        String genre = (String) model.getValueAt(row, 2);
        String date = (String) model.getValueAt(row, 3);
        JTextField nameField = new JTextField(name);
        JComboBox<String> genreBox = new JComboBox<>(genres);
        JTextField customGenreField = new JTextField();
        boolean isOther = java.util.Arrays.stream(genres).noneMatch(g -> g.equalsIgnoreCase(genre));
        genreBox.setSelectedItem(isOther ? "Other" : genre);
        customGenreField.setVisible(isOther);
        if (isOther) customGenreField.setText(genre);
        JTextField dateField = new JTextField(date);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Artwork Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreBox);
        panel.add(customGenreField);
        panel.add(new JLabel("Date (DD-MM-YYYY):"));
        panel.add(dateField);
        genreBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boolean selOther = genreBox.getSelectedItem().equals("Other");
                customGenreField.setVisible(selOther);
                panel.revalidate();
                panel.repaint();
            }
        });
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Artwork", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newGenre = (String) genreBox.getSelectedItem();
            if (newGenre.equals("Other")) {
                newGenre = customGenreField.getText().trim();
            }
            String newDate = dateField.getText().trim();
            if (newName.isEmpty() || newGenre.isEmpty() || newDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedDate = LocalDate.parse(newDate, formatter);
                java.util.List<Artwork> found = manager.searchByName(name).stream().filter(a -> a.getAuthor().getName().equals(author)).toList();
                if (!found.isEmpty()) {
                    Artwork art = found.get(0);
                    art.setName(newName);
                    art.setGenre(newGenre);
                    art.setCreationDate(parsedDate);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use DD-MM-YYYY.");
            }
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
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (ArrayList<Artwork> list : manager.getArtworksByAuthor().values()) {
            for (Artwork a : list)
                model.addRow(new Object[]{a.getName(), a.getAuthor().getName(), a.getGenre(), a.getCreationDate().format(formatter)});
        }
    }
}
