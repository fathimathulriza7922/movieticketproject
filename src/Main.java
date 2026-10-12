import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

class movieTicketBooking extends JFrame {

    private static final String IMAGE_FOLDER =
            "C:\\Users\\hp\\IdeaProjects\\poject\\resources\\";

    private final Color bg = new Color(9, 14, 27);
    private final Color card = new Color(20, 29, 47);
    private final Color fieldBg = new Color(12, 20, 35);
    private final Color gold = new Color(244, 190, 76);
    private final Color white = new Color(245, 247, 252);
    private final Color muted = new Color(158, 172, 194);
    private final Color fieldBorder = new Color(54, 70, 96);

    private JTextField nameField, dateField, ticketField;
    private JComboBox<String> movieBox, timeBox, languageBox;
    private JButton seatButton;
    private JLabel posterLabel, totalLabel, statusLabel;
    private Timer posterTimer;
    private float posterFade = 0f;
    private final int ticketPrice = 150;
    private final int maxSeats = 100;
    private final Set<String> selectedSeats = new LinkedHashSet<>();

    public movieTicketBooking() {
        setTitle("CINEVERSE | Premium Movie Booking");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setSize(1100, 850);
        setMinimumSize(new Dimension(960, 750));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        movieBox.addActionListener(e -> loadPoster());

        ticketField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal();
                    }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal();
                    }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        updateTotal();
                    }
                });

        loadPoster();
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(15, 23, 40));
        p.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);

        JLabel mark = new JLabel("C", SwingConstants.CENTER);
        mark.setFont(new Font("SansSerif", Font.BOLD, 25));
        mark.setForeground(bg);
        mark.setBackground(gold);
        mark.setOpaque(true);
        mark.setPreferredSize(new Dimension(44, 44));

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("CINEVERSE");
        name.setFont(new Font("SansSerif", Font.BOLD, 21));
        name.setForeground(white);

        JLabel tagline = new JLabel("EVERY FRAME. EVERY FEELING.");
        tagline.setFont(new Font("SansSerif", Font.BOLD, 9));
        tagline.setForeground(muted);

        names.add(name);
        names.add(Box.createVerticalStrut(4));
        names.add(tagline);
        brand.add(mark);
        brand.add(names);

        JLabel badge = new JLabel("  ✦ PREMIUM CINEMA  ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setForeground(gold);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(104, 83, 46)),
                new EmptyBorder(10, 8, 10, 8)));

        p.add(brand, BorderLayout.WEST);
        p.add(badge, BorderLayout.EAST);
        return p;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(25, 0));
        body.setBackground(bg);
        body.setBorder(new EmptyBorder(25, 28, 22, 28));

        JPanel left = new JPanel(new BorderLayout(0, 16));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(295, 500));

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("NOW SHOWING  /  YOUR NEXT ESCAPE");
        eyebrow.setForeground(gold);
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel headline = new JLabel("<html>Make it a<br>movie night.</html>");
        headline.setForeground(white);
        headline.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel sub = new JLabel(
                "<html><div style='width:245px'>Choose your film, pick your seat,<br>"
                        + "and let the story begin.</div></html>");
        sub.setForeground(muted);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        intro.add(eyebrow);
        intro.add(Box.createVerticalStrut(10));
        intro.add(headline);
        intro.add(Box.createVerticalStrut(8));
        intro.add(sub);

        JPanel posterCard = new JPanel(new BorderLayout());
        posterCard.setBackground(card);
        posterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(48, 63, 88)),
                new EmptyBorder(8, 8, 8, 8)));

        posterLabel = new FadeLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setForeground(muted);
        posterLabel.setText("Loading poster...");
        posterCard.add(posterLabel, BorderLayout.CENTER);

        JLabel caption = new JLabel("  MOVIE SPOTLIGHT");
        caption.setForeground(muted);
        caption.setFont(new Font("SansSerif", Font.BOLD, 9));

        JPanel posterWrap = new JPanel(new BorderLayout(0, 8));
        posterWrap.setOpaque(false);
        posterWrap.add(posterCard, BorderLayout.CENTER);
        posterWrap.add(caption, BorderLayout.SOUTH);

        left.add(intro, BorderLayout.NORTH);
        left.add(posterWrap, BorderLayout.CENTER);

        body.add(left, BorderLayout.WEST);
        body.add(createForm(), BorderLayout.CENTER);
        return body;
    }

    private JPanel createForm() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(card);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(43, 58, 82)),
                new EmptyBorder(22, 24, 18, 24)));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Book your experience");
        title.setForeground(white);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel description = new JLabel(
                "Your perfect seat is only a few details away.");
        description.setForeground(muted);
        description.setFont(new Font("SansSerif", Font.PLAIN, 12));

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(description);

        JPanel fields = new JPanel(new GridLayout(0, 2, 14, 14));
        fields.setOpaque(false);

        nameField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        ticketField = new JTextField("1");

        movieBox = new JComboBox<>(new String[]{
                "Avatar", "Avengers: Endgame", "Leo"
        });
        timeBox = new JComboBox<>(new String[]{
                "10:00 AM", "2:00 PM", "6:00 PM", "9:00 PM"
        });
        languageBox = new JComboBox<>(new String[]{
                "Malayalam", "English", "Tamil", "Hindi"
        });

        seatButton = makeButton("SELECT SEATS (CLICK HERE)", fieldBg, white);
        seatButton.setPreferredSize(new Dimension(190, 42));
        seatButton.setMinimumSize(new Dimension(150, 42));
        seatButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        seatButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorder, 1),
                new EmptyBorder(4, 10, 4, 10)));
        seatButton.addActionListener(e -> selectSeats());

        styleField(nameField);
        styleField(dateField);
        styleField(ticketField);
        styleCombo(movieBox);
        styleCombo(timeBox);
        styleCombo(languageBox);

        addField(fields, "CUSTOMER NAME", nameField);
        addField(fields, "SELECT MOVIE", movieBox);
        addField(fields, "SHOW DATE (YYYY-MM-DD)", dateField);
        addField(fields, "SHOW TIME", timeBox);
        addField(fields, "TICKET QUANTITY", ticketField);
        addField(fields, "SELECT SEATS", seatButton);
        addField(fields, "AUDIO LANGUAGE", languageBox);

        JPanel totalCard = new JPanel(new BorderLayout(0, 12));
        totalCard.setBackground(new Color(12, 20, 35));
        totalCard.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel totalStack = new JPanel();
        totalStack.setOpaque(false);
        totalStack.setLayout(new BoxLayout(totalStack, BoxLayout.Y_AXIS));

        JLabel totalCaption = new JLabel("TOTAL AMOUNT");
        totalCaption.setForeground(muted);
        totalCaption.setFont(new Font("SansSerif", Font.BOLD, 10));

        totalLabel = new JLabel("Rs. 150");
        totalLabel.setForeground(gold);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel rate = new JLabel("Rs. 150 per ticket");
        rate.setForeground(muted);
        rate.setFont(new Font("SansSerif", Font.PLAIN, 10));

        totalStack.add(totalCaption);
        totalStack.add(Box.createVerticalStrut(4));
        totalStack.add(totalLabel);
        totalStack.add(Box.createVerticalStrut(2));
        totalStack.add(rate);
        totalCard.add(totalStack, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 9, 0));
        buttons.setOpaque(false);

        JButton book = makeButton("CREATE MY TICKET", gold, bg);
        JButton reset = makeButton("RESET", new Color(49, 63, 84), white);
        JButton exit = makeButton("EXIT", new Color(128, 46, 58), white);

        buttons.add(book);
        buttons.add(reset);
        buttons.add(exit);
        totalCard.add(buttons, BorderLayout.SOUTH);

        statusLabel = new JLabel(
                "Ready when you are — your cinema experience awaits.");
        statusLabel.setForeground(muted);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        bottom.add(totalCard, BorderLayout.CENTER);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        outer.add(titlePanel, BorderLayout.NORTH);
        outer.add(fields, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);

        book.addActionListener(e -> createTicket());
        reset.addActionListener(e -> resetForm());
        exit.addActionListener(e -> confirmExit());

        return outer;
    }

    private void addField(JPanel panel, String labelText, JComponent input) {
        JLabel label = new JLabel(labelText);
        label.setForeground(muted);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        panel.add(label);
        panel.add(input);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(white);
        field.setCaretColor(white);
        field.setBackground(fieldBg);
        field.setOpaque(true);
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorder, 1),
                new EmptyBorder(4, 10, 4, 10)));
        field.setPreferredSize(new Dimension(190, 42));
        field.setMinimumSize(new Dimension(150, 42));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setForeground(white);
        combo.setBackground(fieldBg);
        combo.setOpaque(false);
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(190, 42));
        combo.setMinimumSize(new Dimension(150, 42));

        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = super.createArrowButton();
                b.setBackground(fieldBg);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g,
                                                    Rectangle bounds, boolean hasFocus) {
                g.setColor(fieldBg);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("SansSerif", Font.PLAIN, 13));
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(8, 10, 8, 10));
                if (isSelected) {
                    label.setBackground(new Color(35, 48, 71));
                    label.setForeground(gold);
                } else {
                    label.setBackground(fieldBg);
                    label.setForeground(white);
                }
                return label;
            }
        });

        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fieldBorder),
                new EmptyBorder(2, 6, 2, 6)));
    }

    // ക്ലിക്ക് ചെയ്യുമ്പോൾ ബ്ലിങ്കിങ് ഇല്ലാതെ ഒരേ ബാക്ക്ഗ്രൗണ്ട് നിലനിർത്തുന്ന ബട്ടൺ
    private JButton makeButton(String text, Color color, Color foreground) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 10));
        b.setForeground(foreground);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(12, 8, 12, 8));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(color.brighter());
                b.repaint();
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(color);
                b.repaint();
            }
            public void mousePressed(MouseEvent e) {
                b.setBackground(color);
                b.repaint();
            }
        });
        return b;
    }

    private JPanel createFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(6, 10, 19));
        p.setBorder(new EmptyBorder(12, 28, 12, 28));

        JLabel left = new JLabel("CINEVERSE  ©  EVERY FRAME. EVERY FEELING.");
        left.setForeground(new Color(118, 132, 154));
        left.setFont(new Font("SansSerif", Font.BOLD, 9));

        JLabel right = new JLabel("PREMIUM MOVIE EXPERIENCE  ✦");
        right.setForeground(gold);
        right.setFont(new Font("SansSerif", Font.BOLD, 9));

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void selectSeats() {
        int quantity;
        try {
            quantity = Integer.parseInt(ticketField.getText().trim());
            if (quantity < 1 || quantity > maxSeats) {
                warning("Please enter a ticket quantity between 1 and " + maxSeats + ".");
                ticketField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            warning("Please enter a valid ticket quantity first.");
            ticketField.requestFocus();
            return;
        }

        JDialog dialog = new JDialog(this, "Select Your Seats", true);
        dialog.setSize(750, 680);
        dialog.setMinimumSize(new Dimension(700, 620));
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(bg);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "SELECT " + quantity + " SEAT(S)", SwingConstants.CENTER);
        heading.setForeground(gold);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setBorder(new EmptyBorder(15, 10, 10, 10));

        JLabel screen = new JLabel("SCREEN", SwingConstants.CENTER);
        screen.setForeground(muted);
        screen.setFont(new Font("SansSerif", Font.BOLD, 11));
        screen.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel grid = new JPanel(new GridLayout(10, 10, 6, 6));
        grid.setBackground(bg);
        grid.setBorder(new EmptyBorder(10, 18, 10, 18));

        Map<String, JCheckBox> checks = new LinkedHashMap<>();
        for (int row = 0; row < 10; row++) {
            char rowName = (char) ('A' + row);
            for (int col = 1; col <= 10; col++) {
                String seat = rowName + String.valueOf(col);
                JCheckBox check = new JCheckBox(seat);
                check.setHorizontalAlignment(SwingConstants.CENTER);
                check.setFont(new Font("SansSerif", Font.BOLD, 10));
                check.setForeground(white);
                check.setBackground(card);
                check.setOpaque(true);
                check.setFocusPainted(false);
                check.setSelected(selectedSeats.contains(seat));
                checks.put(seat, check);
                grid.add(check);
            }
        }

        JLabel countLabel = new JLabel("", SwingConstants.CENTER);
        countLabel.setForeground(muted);

        Runnable updateCount = () -> {
            int count = 0;
            for (JCheckBox box : checks.values()) {
                if (box.isSelected()) count++;
            }
            countLabel.setText("Selected: " + count + " / " + quantity);
        };

        for (JCheckBox check : checks.values()) {
            check.addActionListener(e -> {
                int count = 0;
                for (JCheckBox box : checks.values()) {
                    if (box.isSelected()) count++;
                }
                if (count > quantity) {
                    check.setSelected(false);
                    warning("You can select only " + quantity + " seats.");
                }
                updateCount.run();
            });
        }
        updateCount.run();

        JButton confirm = makeButton("CONFIRM SEATS", gold, bg);
        JButton cancel = makeButton("CANCEL", new Color(49, 63, 84), white);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(10, 18, 15, 18));
        buttons.add(confirm);
        buttons.add(cancel);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(countLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        confirm.addActionListener(e -> {
            Set<String> newSeats = new LinkedHashSet<>();
            for (Map.Entry<String, JCheckBox> entry : checks.entrySet()) {
                if (entry.getValue().isSelected()) {
                    newSeats.add(entry.getKey());
                }
            }

            if (newSeats.size() != quantity) {
                warning("Please select exactly " + quantity + " seats.");
                return;
            }

            selectedSeats.clear();
            selectedSeats.addAll(newSeats);
            seatButton.setText("SEATS: " + String.join(", ", selectedSeats));
            seatButton.setForeground(gold);
            dialog.dispose();
        });

        cancel.addActionListener(e -> dialog.dispose());

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(screen, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);

        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(center, BorderLayout.CENTER);
        dialog.add(south, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private java.net.URL findPoster(String filename) {
        java.net.URL resource = getClass().getResource("/" + filename);
        if (resource != null) return resource;

        try {
            java.io.File f = new java.io.File(IMAGE_FOLDER + filename);
            if (f.isFile()) return f.toURI().toURL();
        } catch (Exception ignored) {
        }
        return null;
    }

    private void loadPoster() {
        if (posterTimer != null && posterTimer.isRunning()) {
            posterTimer.stop();
        }

        String selected = (String) movieBox.getSelectedItem();
        String filename;

        if ("Avatar".equals(selected)) {
            filename = "avata.jfif";
        } else if ("Avengers: Endgame".equals(selected)) {
            filename = "ven.jfif";
        } else {
            filename = "leo.jfif";
        }

        java.net.URL url = findPoster(filename);
        if (url == null) {
            posterLabel.setIcon(null);
            posterLabel.setText("<html><center>POSTER NOT FOUND<br><br>"
                    + filename + "<br><br>Check your resources folder."
                    + "</center></html>");
            System.out.println("Poster missing: " + IMAGE_FOLDER + filename);
            return;
        }

        ImageIcon source = new ImageIcon(url);
        if (source.getIconWidth() <= 0) {
            posterLabel.setIcon(null);
            posterLabel.setText("Could not read poster.");
            return;
        }

        Image scaled = source.getImage().getScaledInstance(
                255, 350, Image.SCALE_SMOOTH);

        posterFade = 0f;
        ((FadeLabel) posterLabel).setFade(0f);
        posterLabel.setText("");
        posterLabel.setIcon(new ImageIcon(scaled));

        posterTimer = new Timer(25, e -> {
            posterFade = Math.min(1f, posterFade + 0.10f);
            ((FadeLabel) posterLabel).setFade(posterFade);
            if (posterFade >= 1f) posterTimer.stop();
        });
        posterTimer.start();
    }

    private void updateTotal() {
        try {
            int quantity = Integer.parseInt(ticketField.getText().trim());
            if (quantity >= 1 && quantity <= maxSeats) {
                totalLabel.setText("Rs. " + (ticketPrice * quantity));
            } else {
                totalLabel.setText("Rs. 0");
            }
        } catch (NumberFormatException e) {
            totalLabel.setText("Rs. 0");
        }
    }

    private void createTicket() {
        String customer = nameField.getText().trim();

        if (customer.isEmpty()) {
            warning("Please enter the customer name.");
            nameField.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(ticketField.getText().trim());
            if (quantity < 1 || quantity > maxSeats) {
                warning("Please enter a ticket quantity between 1 and " + maxSeats + ".");
                ticketField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            warning("Enter a valid ticket quantity.");
            ticketField.requestFocus();
            return;
        }

        if (selectedSeats.size() != quantity) {
            warning("Please select exactly " + quantity + " seats.");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
            if (date.isBefore(LocalDate.now())) {
                warning("Show date cannot be in the past.");
                dateField.requestFocus();
                return;
            }
        } catch (DateTimeParseException e) {
            warning("Use date format YYYY-MM-DD, for example 2026-10-10.");
            dateField.requestFocus();
            return;
        }

        String bookingId = UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        String movie = (String) movieBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();
        String seats = String.join(", ", selectedSeats);
        String language = (String) languageBox.getSelectedItem();
        int total = ticketPrice * quantity;

        showTicketDialog(bookingId, customer, movie, date.toString(),
                time, seats, language, quantity, total);

        statusLabel.setText("Ticket " + bookingId + " created successfully.");
    }

    private void showTicketDialog(String id, String customer, String movie,
                                  String date, String time, String seats,
                                  String language, int quantity, int total) {
        JDialog dialog = new JDialog(this, "Your Cineverse E-Ticket", true);
        dialog.setSize(760, 560);
        dialog.setMinimumSize(new Dimension(700, 520));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel ticket = new JPanel(new BorderLayout(18, 0));
        ticket.setBackground(card);
        ticket.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(73, 88, 113)),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setOpaque(false);

        JLabel top = new JLabel("CINEVERSE  /  ADMIT ONE");
        top.setForeground(gold);
        top.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel bigTitle = new JLabel("<html><div style='width:300px'>"
                + escapeHtml(movie) + "</div></html>");
        bigTitle.setForeground(white);
        bigTitle.setFont(new Font("SansSerif", Font.BOLD, 25));

        JPanel details = new JPanel(new GridLayout(0, 2, 10, 13));
        details.setOpaque(false);
        addDetail(details, "BOOKING ID", id);
        addDetail(details, "CUSTOMER", customer);
        addDetail(details, "SHOW DATE", date);
        addDetail(details, "SHOW TIME", time);
        addDetail(details, "SEATS", seats);
        addDetail(details, "LANGUAGE", language);
        addDetail(details, "TICKETS", String.valueOf(quantity));
        addDetail(details, "PRICE / TICKET", "Rs. " + ticketPrice);

        JPanel price = new JPanel(new BorderLayout());
        price.setOpaque(false);
        price.setBorder(new EmptyBorder(14, 0, 0, 0));

        JLabel totalText = new JLabel("TOTAL AMOUNT");
        totalText.setForeground(muted);
        totalText.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel amount = new JLabel("Rs. " + total);
        amount.setForeground(gold);
        amount.setFont(new Font("SansSerif", Font.BOLD, 27));

        JPanel priceStack = new JPanel();
        priceStack.setOpaque(false);
        priceStack.setLayout(new BoxLayout(priceStack, BoxLayout.Y_AXIS));
        priceStack.add(totalText);
        priceStack.add(Box.createVerticalStrut(4));
        priceStack.add(amount);
        price.add(priceStack, BorderLayout.WEST);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(top);
        heading.add(Box.createVerticalStrut(8));
        heading.add(bigTitle);
        heading.add(Box.createVerticalStrut(18));

        left.add(heading, BorderLayout.NORTH);
        left.add(details, BorderLayout.CENTER);
        left.add(price, BorderLayout.SOUTH);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel qrTitle = new JLabel("YOUR TICKET QR");
        qrTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrTitle.setForeground(gold);
        qrTitle.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel qrImage = new JLabel();
        qrImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrImage.setPreferredSize(new Dimension(190, 190));
        qrImage.setMaximumSize(new Dimension(190, 190));
        qrImage.setOpaque(true);
        qrImage.setBackground(Color.WHITE);
        qrImage.setHorizontalAlignment(SwingConstants.CENTER);
        qrImage.setIcon(loadQrCode(id));

        JLabel qrHint = new JLabel("<html><center>Scan to read booking ID<br>"
                + id + "</center></html>");
        qrHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrHint.setForeground(muted);
        qrHint.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JLabel note = new JLabel("<html><center>Keep this ticket ready<br>"
                + "at the cinema entrance.</center></html>");
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        note.setForeground(white);
        note.setFont(new Font("SansSerif", Font.PLAIN, 11));

        right.add(qrTitle);
        right.add(Box.createVerticalStrut(12));
        right.add(qrImage);
        right.add(Box.createVerticalStrut(10));
        right.add(qrHint);
        right.add(Box.createVerticalStrut(16));
        right.add(note);

        ticket.add(left, BorderLayout.CENTER);
        ticket.add(right, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton save = makeButton("SAVE TICKET DETAILS",
                new Color(49, 63, 84), white);
        JButton close = makeButton("DONE", gold, bg);

        save.addActionListener(e -> saveTicket(id, customer, movie, date,
                time, seats, language, quantity, total));
        close.addActionListener(e -> dialog.dispose());

        bottom.add(save);
        bottom.add(close);

        root.add(ticket, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void addDetail(JPanel panel, String label, String value) {
        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setForeground(muted);
        l.setFont(new Font("SansSerif", Font.BOLD, 9));

        JLabel v = new JLabel("<html><div style='width:145px'>"
                + escapeHtml(value) + "</div></html>");
        v.setForeground(white);
        v.setFont(new Font("SansSerif", Font.BOLD, 12));

        item.add(l);
        item.add(Box.createVerticalStrut(4));
        item.add(v);
        panel.add(item);
    }

    private ImageIcon loadQrCode(String bookingId) {
        try {
            String data = URLEncoder.encode(
                    "CINEVERSE-BOOKING:" + bookingId,
                    StandardCharsets.UTF_8.name());
            String url = "https://api.qrserver.com/v1/create-qr-code/"
                    + "?size=190x190&data=" + data;
            ImageIcon icon = new ImageIcon(new java.net.URL(url));
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                return icon;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void saveTicket(String id, String customer, String movie,
                            String date, String time, String seats,
                            String language, int quantity, int total) {
        String text = "CINEVERSE E-TICKET\n"
                + "Booking ID: " + id + "\n"
                + "Customer: " + customer + "\n"
                + "Movie: " + movie + "\n"
                + "Show date: " + date + "\n"
                + "Show time: " + time + "\n"
                + "Seats: " + seats + "\n"
                + "Language: " + language + "\n"
                + "Tickets: " + quantity + "\n"
                + "Price per ticket: Rs. " + ticketPrice + "\n"
                + "Total amount: Rs. " + total + "\n"
                + "QR data: CINEVERSE-BOOKING:" + id + "\n";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save your ticket details");
        chooser.setSelectedFile(new java.io.File("Cineverse_Ticket_" + id + ".txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter out = new java.io.PrintWriter(
                    chooser.getSelectedFile(), StandardCharsets.UTF_8.name())) {
                out.print(text);
                JOptionPane.showMessageDialog(this,
                        "Ticket details saved successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save ticket: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void warning(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Check your details", JOptionPane.WARNING_MESSAGE);
    }

    private void resetForm() {
        nameField.setText("");
        dateField.setText(LocalDate.now().toString());
        ticketField.setText("1");
        movieBox.setSelectedIndex(0);
        timeBox.setSelectedIndex(0);
        languageBox.setSelectedIndex(0);

        selectedSeats.clear();
        seatButton.setText("SELECT SEATS (CLICK HERE)");
        seatButton.setForeground(white);

        updateTotal();
        nameField.requestFocus();
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit Cineverse?",
                "Exit Cineverse", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (posterTimer != null) posterTimer.stop();
            dispose();
        }
    }

    private static class FadeLabel extends JLabel {
        private float alpha = 1f;

        FadeLabel() {
            super("", SwingConstants.CENTER);
        }

        void setFade(float value) {
            alpha = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new movieTicketBooking();
        });
    }
}