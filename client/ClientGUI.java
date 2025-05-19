package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField, bookIdField;
    private JTextArea outputArea;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // Dark mode state
    private boolean darkMode = false;

    // Ngjyrat për light/dark mode
    private Color primary = new Color(44, 62, 80);
    private Color secondary = new Color(236, 240, 241);
    private Color accent = new Color(41, 128, 185);
    private Color accent2 = new Color(52, 152, 219);
    private Color danger = new Color(192, 57, 43);

    private Color darkPrimary = new Color(24, 26, 27);
    private Color darkSecondary = new Color(34, 40, 49);
    private Color darkAccent = new Color(0, 173, 181);
    private Color darkAccent2 = new Color(57, 62, 70);

    private Font font = new Font("Segoe UI", Font.PLAIN, 16);
    private Font fontBold = new Font("Segoe UI", Font.BOLD, 18);

    // Komponentët që ndryshojnë ngjyrë në dark mode
    private JPanel headerPanel, commandPanel, mainPanel;
    private JScrollPane tableScroll, outputScroll;
    private JButton searchBtn, borrowBtn, returnBtn, listBtn, exitBtn, darkModeBtn;

    public ClientGUI() {
        // Look and Feel modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        setTitle("Libraria Online - Klienti");
        setSize(750, 570);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // HEADER me ikonë dhe emër
        headerPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = darkMode
                        ? new GradientPaint(0, 0, darkAccent2, getWidth(), getHeight(), darkAccent)
                        : new GradientPaint(0, 0, accent, getWidth(), getHeight(), accent2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(750, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        JLabel iconLabel = new JLabel("\uD83D\uDCDA"); // Ikonë libri unicode
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        JLabel titleLabel = new JLabel("Libraria Online");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Dark mode toggle button
        darkModeBtn = new JButton("🌙");
        darkModeBtn.setFocusPainted(false);
        darkModeBtn.setBackground(accent2);
        darkModeBtn.setForeground(Color.WHITE);
        darkModeBtn.setFont(fontBold);
        darkModeBtn.setBorder(new RoundBorder(accent2, 18));
        darkModeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerPanel.add(Box.createHorizontalStrut(350));
        headerPanel.add(darkModeBtn);

        // Paneli i komandave me gradient
        commandPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = darkMode
                        ? new GradientPaint(0, 0, darkPrimary, getWidth(), getHeight(), darkAccent2)
                        : new GradientPaint(0, 0, primary, getWidth(), getHeight(), accent2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        commandPanel.setLayout(new GridBagLayout());
        commandPanel.setOpaque(false);
        commandPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel searchLabel = new JLabel("Kërko libër:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(fontBold);
        gbc.gridx = 0; gbc.gridy = 0;
        commandPanel.add(searchLabel, gbc);

        searchField = new JTextField();
        searchField.setBackground(secondary);
        searchField.setForeground(primary);
        searchField.setFont(font);
        searchField.setBorder(new RoundBorder(accent2, 12));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        commandPanel.add(searchField, gbc);

        searchBtn = createButton("🔍 Kërko", accent2, fontBold);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        commandPanel.add(searchBtn, gbc);

        JLabel idLabel = new JLabel("ID librit:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(fontBold);
        gbc.gridx = 0; gbc.gridy = 1;
        commandPanel.add(idLabel, gbc);

        bookIdField = new JTextField();
        bookIdField.setBackground(secondary);
        bookIdField.setForeground(primary);
        bookIdField.setFont(font);
        bookIdField.setBorder(new RoundBorder(accent2, 12));
        gbc.gridx = 1; gbc.gridy = 1;
        commandPanel.add(bookIdField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        borrowBtn = createButton("📚 Huazo", accent2, fontBold);
        returnBtn = createButton("↩️ Kthe", accent2, fontBold);
        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        gbc.gridx = 2; gbc.gridy = 1;
        commandPanel.add(btnPanel, gbc);

        listBtn = createButton("📋 Listo librat", accent2, fontBold);
        exitBtn = createButton("❌ Dil", danger, fontBold);
        gbc.gridx = 1; gbc.gridy = 2;
        commandPanel.add(listBtn, gbc);
        gbc.gridx = 2; gbc.gridy = 2;
        commandPanel.add(exitBtn, gbc);

        // Tabela e librave
        String[] columns = {"ID", "Titulli", "Autori", "Në dispozicion"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setBackground(secondary);
        table.setForeground(primary);
        table.setSelectionBackground(accent2);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(24);
        table.setFont(font);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(new EmptyBorder(0,0,0,0));
        table.getTableHeader().setBackground(accent2);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new EmptyBorder(0,0,0,0));

        // Zona per outputin me border të rrumbullakët
        outputArea = new JTextArea(6, 50);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(245, 246, 250));
        outputArea.setForeground(primary);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setBorder(new RoundBorder(accent2, 10));
        outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(new EmptyBorder(8,0,0,0));

        // Layout kryesor
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(secondary);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(commandPanel, BorderLayout.CENTER);
        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.NORTH);
        add(outputScroll, BorderLayout.CENTER);

        // Animacion slide-in kur hapet dritarja (pa setOpacity)
        mainPanel.setLocation(0, 50);
        Timer slideIn = new Timer(10, null);
        slideIn.addActionListener(new ActionListener() {
            int y = 50;
            public void actionPerformed(ActionEvent e) {
                y -= 5;
                if (y <= 0) {
                    y = 0;
                    slideIn.stop();
                }
                mainPanel.setLocation(0, y);
            }
        });
        slideIn.start();

        // Lidhja me serverin dhe eventet
        try {
            socket = new Socket("localhost", 6000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            String welcome = input.readLine();
            outputArea.append(welcome + "\n");

            searchBtn.addActionListener(e -> searchBook());
            borrowBtn.addActionListener(e -> borrowBook());
            returnBtn.addActionListener(e -> returnBook());
            listBtn.addActionListener(e -> listBooks());
            exitBtn.addActionListener(e -> exitClient());

            darkModeBtn.addActionListener(e -> toggleDarkMode());

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String id = tableModel.getValueAt(row, 0).toString();
                        bookIdField.setText(id);
                    }
                }
            });

            listBooks();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Nuk u lidh me serverin!", "Gabim", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    // Funksion për butona të rrumbullakët me efekt hover
    private JButton createButton(String text, Color bg, Font font) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundBorder(bg, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // Border i rrumbullakët për fushat dhe butonat
    static class RoundBorder extends LineBorder {
        private int radius;
        public RoundBorder(Color color, int radius) {
            super(color, 1, true);
            this.radius = radius;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(lineColor);
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    // Dark mode toggle
    private void toggleDarkMode() {
        darkMode = !darkMode;
        if (darkMode) {
            mainPanel.setBackground(darkSecondary);
            commandPanel.repaint();
            headerPanel.repaint();
            searchField.setBackground(darkSecondary);
            searchField.setForeground(Color.WHITE);
            bookIdField.setBackground(darkSecondary);
            bookIdField.setForeground(Color.WHITE);
            table.setBackground(darkSecondary);
            table.setForeground(Color.WHITE);
            table.setSelectionBackground(darkAccent);
            table.setSelectionForeground(Color.WHITE);
            table.getTableHeader().setBackground(darkAccent);
            table.getTableHeader().setForeground(Color.WHITE);
            outputArea.setBackground(new Color(40, 44, 52));
            outputArea.setForeground(Color.WHITE);
            searchBtn.setBackground(darkAccent);
            borrowBtn.setBackground(darkAccent);
            returnBtn.setBackground(darkAccent);
            listBtn.setBackground(darkAccent);
            exitBtn.setBackground(danger.darker());
            darkModeBtn.setBackground(darkAccent);
            darkModeBtn.setText("☀️");
        } else {
            mainPanel.setBackground(secondary);
            commandPanel.repaint();
            headerPanel.repaint();
            searchField.setBackground(secondary);
            searchField.setForeground(primary);
            bookIdField.setBackground(secondary);
            bookIdField.setForeground(primary);
            table.setBackground(secondary);
            table.setForeground(primary);
            table.setSelectionBackground(accent2);
            table.setSelectionForeground(Color.WHITE);
            table.getTableHeader().setBackground(accent2);
            table.getTableHeader().setForeground(Color.WHITE);
            outputArea.setBackground(new Color(245, 246, 250));
            outputArea.setForeground(primary);
            searchBtn.setBackground(accent2);
            borrowBtn.setBackground(accent2);
            returnBtn.setBackground(accent2);
            listBtn.setBackground(accent2);
            exitBtn.setBackground(danger);
            darkModeBtn.setBackground(accent2);
            darkModeBtn.setText("🌙");
        }
    }

    // --- pjesa tjetër e kodit identike ---
    private void sendCommand(String cmd) {
        output.println(cmd);
    }

    private String readResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        socket.setSoTimeout(500);
        try {
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                sb.append(line).append("\n");
            }
        } catch (java.net.SocketTimeoutException e) {}
        socket.setSoTimeout(0);
        return sb.toString().trim();
    }

    private void searchBook() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shkruajni një fjalë kyçe për kërkimin.");
            return;
        }
        sendCommand("KERKO;" + keyword);
        try {
            String response = readResponse();
            outputArea.append("Rezultatet e kërkimit për '" + keyword + "':\n" + response + "\n");
            updateTableFromResponse(response);
        } catch (IOException e) {
            outputArea.append("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void borrowBook() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shkruajni ID-në e librit për huazim.");
            return;
        }
        sendCommand("HUAZO;" + id);
        try {
            String response = input.readLine();
            outputArea.append("Serveri: " + response + "\n");
            listBooks();
        } catch (IOException e) {
            outputArea.append("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void returnBook() {
        String id = bookIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shkruajni ID-në e librit për kthim.");
            return;
        }
        sendCommand("KTHE;" + id);
        try {
            String response = input.readLine();
            outputArea.append("Serveri: " + response + "\n");
            listBooks();
        } catch (IOException e) {
            outputArea.append("Gabim gjatë marrjes së përgjigjes.\n");
        }
    }

    private void listBooks() {
        sendCommand("LISTO");
        try {
            String response = readResponse();
            outputArea.append("Lista e librave:\n" + response + "\n");
            updateTableFromResponse(response);
        } catch (IOException e) {
            outputArea.append("Gabim gjatë marrjes së listës.\n");
        }
    }

    private void exitClient() {
        sendCommand("EXIT");
        try {
            String response = input.readLine();
            outputArea.append(response + "\n");
        } catch (IOException ignored) {}
        try {
            socket.close();
        } catch (IOException ignored) {}
        System.exit(0);
    }

    private void updateTableFromResponse(String response) {
        tableModel.setRowCount(0);
        if (response.isEmpty()) return;

        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;

            String id = parts[0].replace("ID:", "").trim();
            String title = parts[1].replace("Titulli:", "").trim();
            String author = parts[2].replace("Autori:", "").trim();
            String available = parts[3].replace("Në dispozicion:", "").trim();

            tableModel.addRow(new Object[]{id, title, author, available});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
}