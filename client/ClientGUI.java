package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea logArea;
    private JTextField inputField;
    private JButton searchBtn, borrowBtn, returnBtn, listBtn;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public ClientGUI() {
        setTitle("Libraria Online - Klienti");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
        connectToServer();
        doList();

    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        add(mainPanel);

        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        String[] cols = {"ID", "Titulli", "Autori", "Në Disponibilitet"};
        tableModel = new DefaultTableModel(cols, 0);
        booksTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(booksTable);

        JPanel commandPanel = new JPanel();
        inputField = new JTextField(20);

        searchBtn = new JButton("Kërko");
        borrowBtn = new JButton("Huazo");
        returnBtn = new JButton("Kthe");
        listBtn = new JButton("Lista e Librave");

        commandPanel.add(new JLabel("Fjalë kyçe / ID:"));
        commandPanel.add(inputField);
        commandPanel.add(searchBtn);
        commandPanel.add(borrowBtn);
        commandPanel.add(returnBtn);
        commandPanel.add(listBtn);

        mainPanel.add(commandPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(logScroll, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> doSearch());
        borrowBtn.addActionListener(e -> doBorrow());
        returnBtn.addActionListener(e -> doReturn());
        listBtn.addActionListener(e -> doList());
        inputField.addActionListener(e -> {
            doSearch();
            inputField.setText(""); 
        });

    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 6000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            logArea.append(input.readLine() + "\n");

            new Thread(this::listenServer).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Nuk mund të lidhet me serverin.", "Gabim", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void listenServer() {
        try {
            String line;
            while ((line = input.readLine()) != null) {
                String currentLine = line; // për closure të sigurt
                SwingUtilities.invokeLater(() -> {
                    logArea.append("Serveri: " + currentLine + "\n");
                    if (currentLine.startsWith("ID:")) {
                        updateTable();
                    }
                });
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> logArea.append("Lidhja me serverin u ndërpre.\n"));
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Fshij tabelën

        String[] allLines = logArea.getText().split("\n");
        for (String l : allLines) {
            if (l.startsWith("Serveri: ID:")) {
                String data = l.substring(9).trim(); // heq "Serveri: "
                String[] parts = data.split("\\|");
                if (parts.length == 4) {
                    try {
                        String id = parts[0].split(":")[1].trim();
                        String titulli = parts[1].split(":")[1].trim();
                        String autori = parts[2].split(":")[1].trim();
                        String disponueshmeria = parts[3].split(":")[1].trim();
                        tableModel.addRow(new Object[]{id, titulli, autori, disponueshmeria});
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Nëse ka gabim në format, e anashkalojmë
                    }
                }
            }
        }
    }

    private void sendCommand(String cmd) {
        output.println(cmd);
        logArea.append("Ti: " + cmd + "\n");
    }
    private void doSearch() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) return;

        output.println("search:" + query);
        try {
            String response = input.readLine();
            
            booksTable.setToolTipText(""); // 🧹 Pastro textArea përpara se të shtosh tekstin e ri

            if (response == null || response.trim().isEmpty()) {
            	logArea.setText("Nuk u gjet asnjë libër me këtë emër.");
            } else {
            	logArea.setText(response); // nuk përdorim .append për të mos shtuar poshtë
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void doBorrow() {
        String id = inputField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shkruaj ID-në e librit për huazim.", "Kujdes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sendCommand("HUAZO;" + id);
    }

    private void doReturn() {
        String id = inputField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shkruaj ID-në e librit për kthim.", "Kujdes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sendCommand("KTHE;" + id);
    }

    private void doList() {
        sendCommand("LISTO");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}
