import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlatformDisplay extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable table;
    private DefaultTableModel model;
    private javax.swing.Timer simulationTimer;
    private javax.swing.Timer clockTimer;
    private JLabel clockLabel;

    private java.util.List<String[]> trainData;

    public PlatformDisplay() {
        setTitle("Railway Station Platform Display System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header clock
        clockLabel = new JLabel();
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        clockLabel.setForeground(Color.RED);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        startClock();

        // Initial train data
        trainData = new ArrayList<>();
        trainData.add(new String[]{"1001", "Chennai Express", "10:00", "On Time"});
        trainData.add(new String[]{"1002", "Bangalore Mail", "10:30", "Delayed"});
        trainData.add(new String[]{"1003", "Mumbai Express", "11:00", "Arrival"});
        trainData.add(new String[]{"1004", "Delhi Duronto", "11:30", "Departure"});

        // Card layout for switching pages
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPage(), "Login");
        mainPanel.add(createDashboardPage(), "Dashboard");
        mainPanel.add(createSearchPage(), "Search");
        mainPanel.add(createFilterPage(), "Filter");
        mainPanel.add(createSimulationPage(), "Simulation");

        add(clockLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        showPage("Login");
    }

    // CLOCK
    private void startClock() {
        clockTimer = new javax.swing.Timer(1000, e -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String date = new SimpleDateFormat("EEE, dd MMM yyyy").format(new Date());
            clockLabel.setText("Time: " + time + "    Date: " + date);
        });
        clockTimer.start();
    }

    // LOGIN PAGE
    private JPanel createLoginPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 90, 180));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Railway Station Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridy = 0; panel.add(title, gbc);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        gbc.gridy = 1; panel.add(userField, gbc);
        gbc.gridy = 2; panel.add(passField, gbc);
        gbc.gridy = 3; panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> showPage("Dashboard"));
        return panel;
    }

    // DASHBOARD
    private JPanel createDashboardPage() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 20, 20));
        panel.setBackground(new Color(210, 240, 255));

        JLabel title = new JLabel("Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(title);

        JButton searchBtn = new JButton("Search Trains");
        JButton filterBtn = new JButton("Filter by Status");
        JButton simBtn = new JButton("Simulation");

        styleButton(searchBtn);
        styleButton(filterBtn);
        styleButton(simBtn);

        searchBtn.addActionListener(e -> showPage("Search"));
        filterBtn.addActionListener(e -> showPage("Filter"));
        simBtn.addActionListener(e -> showPage("Simulation"));

        panel.add(searchBtn);
        panel.add(filterBtn);
        panel.add(simBtn);

        return panel;
    }

    // SEARCH PAGE
    private JPanel createSearchPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton backBtn = new JButton("Back");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search Train:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(backBtn);

        String[] cols = {"Train No", "Train Name", "Time", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        refreshTable(trainData);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String text = searchField.getText().trim().toLowerCase();
            java.util.List<String[]> filtered = new ArrayList<>();
            for (String[] row : trainData) {
                if (row[0].toLowerCase().contains(text) || row[1].toLowerCase().contains(text))
                    filtered.add(row);
            }
            refreshTable(filtered);
        });

        backBtn.addActionListener(e -> showPage("Dashboard"));
        return panel;
    }

    // FILTER PAGE (FIXED)
    private JPanel createFilterPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"All", "On Time", "Delayed", "Arrival", "Departure"});
        JButton applyBtn = new JButton("Apply Filter");
        JButton backBtn = new JButton("Back");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Filter by Status:"));
        topPanel.add(statusBox);
        topPanel.add(applyBtn);
        topPanel.add(backBtn);

        String[] cols = {"Train No", "Train Name", "Time", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        refreshTable(trainData);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        applyBtn.addActionListener(e -> {
            String selected = statusBox.getSelectedItem().toString();
            java.util.List<String[]> filtered = new ArrayList<>();

            if (selected.equals("All")) {
                refreshTable(trainData);
            } else {
                for (String[] row : trainData) {
                    if (row[3].equalsIgnoreCase(selected))
                        filtered.add(row);
                }
                refreshTable(filtered);
            }
        });

        backBtn.addActionListener(e -> showPage("Dashboard"));
        return panel;
    }

    // SIMULATION PAGE
    private JPanel createSimulationPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JButton startBtn = new JButton("Start Simulation");
        JButton stopBtn = new JButton("Stop Simulation");
        JButton backBtn = new JButton("Back");

        styleButton(startBtn);
        styleButton(stopBtn);
        styleButton(backBtn);

        JPanel topPanel = new JPanel();
        topPanel.add(startBtn);
        topPanel.add(stopBtn);
        topPanel.add(backBtn);

        String[] cols = {"Train No", "Train Name", "Time", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        refreshTable(trainData);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        startBtn.addActionListener(e -> startSimulation());
        stopBtn.addActionListener(e -> stopSimulation());
        backBtn.addActionListener(e -> showPage("Dashboard"));

        return panel;
    }

    // SIMULATION LOGIC
    private void startSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) return;

        simulationTimer = new javax.swing.Timer(1000, e -> {
            Random r = new Random();
            java.util.List<String> names = Arrays.asList(
                    "Chennai Express", "Coimbatore SF", "Bangalore Mail",
                    "Hyderabad Express", "Delhi Duronto", "Mumbai Rajdhani"
            );

            for (String[] t : trainData) {
                t[0] = String.valueOf(1000 + r.nextInt(900)); // Train No
                t[1] = names.get(r.nextInt(names.size())); // Name
                t[2] = String.format("%02d:%02d", r.nextInt(24), r.nextInt(60)); // Time
                t[3] = switch (r.nextInt(4)) {
                    case 0 -> "On Time";
                    case 1 -> "Delayed";
                    case 2 -> "Arrival";
                    default -> "Departure";
                };
            }
            refreshTable(trainData);
        });

        simulationTimer.start();
    }

    private void stopSimulation() {
        if (simulationTimer != null) simulationTimer.stop();
    }

    // UTILS
    private void refreshTable(java.util.List<String[]> data) {
        model.setRowCount(0);
        for (String[] row : data)
            model.addRow(row);
    }

    private void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(60, 150, 250));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlatformDisplay().setVisible(true));
    }
}
