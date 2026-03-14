import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends JFrame {

    // --- CẤU HÌNH MÀU SẮC  ---
    private final Color PRIMARY_COLOR = new Color(108, 92, 231);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);    // Xanh dương (Chỉnh sửa)
    private final Color PRIMARY_GREEN = new Color(39, 174, 96);    // Tím nhạt
    private final Color ACCENT_COLOR = new Color(162, 155, 254);    // Tím phụ trợ
    private final Color BG_COLOR = new Color(240, 244, 248);        // Nền xám nhạt
    private final Color DELETE_RED = new Color(255, 118, 117);      // Đỏ pastel
    private final Color SUCCESS_GREEN = new Color(85, 239, 196);    // Xanh mint
    private final Color WARNING_ORANGE = new Color(253, 203, 110);  // Vàng cam
    private final Color TEXT_DARK = new Color(45, 52, 54);
    
    private final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);

    // --- CÁC THÀNH PHẦN CHÍNH ---
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContentPanel = new JPanel(cardLayout);
    private String loggedInUser = "";
    
    private List<Task> masterTaskList = new ArrayList<>();
    private Task taskToEdit = null;

    private JPanel taskListPanel;
    private JPanel reportContainerPanel;
    private JLabel lblWelcomeProfile;
    private JTextField searchField;
    private JLabel lblTotalTasks, lblCompletedTasks;

    // Fields cho màn hình EDIT
    private JTextField txtEditTaskName, txtEditDate;
    private JComboBox<String> cbEditCategory, cbEditPriority, cbEditStatus;

    public Main() {
        setTitle("Quản Lý Công Việc Cá Nhân");
        setSize(450, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // DỮ LIỆU MẪU
        masterTaskList.add(new Task("Học Java Swing nâng cao", "Học tập", "Cao", "Đang làm", "10/10/2026"));
        masterTaskList.add(new Task("Mua quà sinh nhật mẹ", "Gia đình", "Trung bình", "Chưa làm", "12/10/2026"));
        masterTaskList.add(new Task("Nộp báo cáo cuối tháng", "Công việc", "Cao", "Hoàn thành", "05/10/2026"));
        
        // ADD CÁC MÀN HÌNH
        mainContentPanel.add(createLoginPanel(), "LOGIN");
        mainContentPanel.add(createHomePanel(), "HOME");
        mainContentPanel.add(createAddPanel(), "ADD");
        mainContentPanel.add(createEditPanel(), "EDIT");
        mainContentPanel.add(createReportPanel(), "REPORT");
        mainContentPanel.add(createProfilePanel(), "PROFILE");

        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "LOGIN");
    }

    // --- 1. MÀN HÌNH ĐĂNG NHẬP ---
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        
        JLabel logo = new JLabel("Công Việc Cá Nhân", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 36));
        logo.setForeground(PRIMARY_COLOR);
        logo.setBounds(0, 120, 450, 50);

        JTextField txtUser = new JTextField();
        txtUser.setBounds(60, 260, 330, 55);
        txtUser.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1), "Tên đăng nhập"));

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(60, 330, 330, 55);
        txtPass.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1), "Mật khẩu"));

        JButton btnLogin = createRoundedButton("ĐĂNG NHẬP", PRIMARY_COLOR, Color.WHITE);
        btnLogin.setBounds(60, 430, 330, 60);

        btnLogin.addActionListener(e -> {
            if (!txtUser.getText().trim().isEmpty()) {
                loggedInUser = txtUser.getText().trim();
                lblWelcomeProfile.setText("Xin Chào, " + loggedInUser);
                add(createBottomNav(), BorderLayout.SOUTH);
                revalidate(); refreshHomeList(); cardLayout.show(mainContentPanel, "HOME");
            }
        });
        
        panel.add(logo); panel.add(txtUser); panel.add(txtPass); panel.add(btnLogin);
        return panel;
    }

    // --- 2. MÀN HÌNH CHÍNH  ---
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // Header
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS)); // Xếp dọc
        top.setBackground(PRIMARY_COLOR);
        top.setPreferredSize(new Dimension(0, 150)); // Chiều cao cố định vừa phải
        top.setBorder(new EmptyBorder(15, 20, 15, 20)); // Căn lề

        // 1. Tiêu đề + Thống kê
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(500, 50)); // Giới hạn chiều cao

        JLabel lblHello = new JLabel("Danh sách công việc");
        lblHello.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblHello.setForeground(Color.WHITE);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statsPanel.setOpaque(false);
        lblTotalTasks = new JLabel("Tổng: 0  |  "); lblTotalTasks.setForeground(new Color(223, 230, 233));
        lblCompletedTasks = new JLabel("Xong: 0"); lblCompletedTasks.setForeground(SUCCESS_GREEN);
        statsPanel.add(lblTotalTasks); statsPanel.add(lblCompletedTasks);

        JPanel textWrap = new JPanel(new GridLayout(2, 1));
        textWrap.setOpaque(false);
        textWrap.add(lblHello); textWrap.add(statsPanel);
        titlePanel.add(textWrap, BorderLayout.CENTER);

        // 2. Thanh tìm kiếm 
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setOpaque(false);
        searchContainer.setMaximumSize(new Dimension(450, 45)); // Chiều cao thanh search
        // Tạo khoảng cách trên dưới cho thanh search
        searchContainer.setBorder(new EmptyBorder(10, 5, 5, 5)); 

        // Panel vẽ nền trắng bo tròn cho thanh search
        JPanel searchBackground = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 50)); // Màu trắng trong suốt nhẹ (Glass effect) hoặc để White hẳn
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        searchBackground.setOpaque(false);
        searchBackground.setBorder(new EmptyBorder(5, 15, 5, 10)); // Padding bên trong thanh search

        JLabel searchIcon = new JLabel("🔍 ");
        searchIcon.setForeground(Color.GRAY);
        
        searchField = new JTextField();
        searchField.setBorder(null);
        searchField.setOpaque(false); // Để lộ nền bo tròn phía sau
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm..."); 
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshHomeList(); }
            public void removeUpdate(DocumentEvent e) { refreshHomeList(); }
            public void changedUpdate(DocumentEvent e) { refreshHomeList(); }
        });

        searchBackground.add(searchIcon, BorderLayout.WEST);
        searchBackground.add(searchField, BorderLayout.CENTER);
        
        searchContainer.add(searchBackground, BorderLayout.CENTER);

        top.add(titlePanel);
        top.add(searchContainer);

        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(BG_COLOR);
        taskListPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(taskListPanel), BorderLayout.CENTER);
        return panel;
    }

    private void refreshHomeList() {
        taskListPanel.removeAll();
        String query = searchField.getText().toLowerCase();
        int total = 0, completed = 0;
        for (Task t : masterTaskList) {
            total++; if(t.status.equals("Hoàn thành")) completed++;
            if (t.title.toLowerCase().contains(query)) {
                taskListPanel.add(createTaskItemUI(t));
                taskListPanel.add(Box.createVerticalStrut(35)); 
            }
        }
        lblTotalTasks.setText("Tổng: " + total + "  |  "); lblCompletedTasks.setText("Xong: " + completed);
        taskListPanel.revalidate(); taskListPanel.repaint();
    }

    private JPanel createTaskItemUI(Task task) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(420, 110));
        item.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setOpaque(false);
        JLabel lblT = new JLabel(task.title); lblT.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel lblM = new JLabel(task.category + " • " + task.deadline);
        lblM.setFont(new Font("SansSerif", Font.PLAIN, 12)); lblM.setForeground(Color.GRAY);
        
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setOpaque(false);
        badgePanel.add(createBadge(task.priority, getPriorityColor(task.priority)));
        badgePanel.add(Box.createHorizontalStrut(8));
        badgePanel.add(createBadge(task.status, getStatusColor(task.status)));
        centerPanel.add(lblT); centerPanel.add(lblM); centerPanel.add(badgePanel);

        // Nút hành động
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 20));
        actionPanel.setOpaque(false);
        
        JButton bCheck = createSmallIconButton("✔", SUCCESS_GREEN);
        bCheck.addActionListener(e -> { task.status = "Hoàn thành"; refreshHomeList(); });

        JButton bEdit = createSmallIconButton("✎", PRIMARY_COLOR);
        bEdit.addActionListener(e -> {
            taskToEdit = task;
            txtEditTaskName.setText(task.title);
            cbEditCategory.setSelectedItem(task.category);
            cbEditPriority.setSelectedItem(task.priority);
            cbEditStatus.setSelectedItem(task.status);
            txtEditDate.setText(task.deadline);
            cardLayout.show(mainContentPanel, "EDIT");
        });

        JButton bDel = createSmallIconButton("✕", DELETE_RED);
        bDel.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Xóa việc này ?", "Xác nhận", 0) == 0){
                masterTaskList.remove(task); refreshHomeList();
            }
        });

        actionPanel.add(bCheck); actionPanel.add(bEdit); actionPanel.add(bDel);
        item.add(centerPanel, BorderLayout.CENTER);
        item.add(actionPanel, BorderLayout.EAST);
        return item;
    }

    // --- 3. MÀN HÌNH THÊM MỚI ---
    private JPanel createAddPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel("THÊM CÔNG VIỆC MỚI", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE); lblTitle.setForeground(PRIMARY_GREEN);
        lblTitle.setBounds(0, 30, 450, 30);

        JTextField txtTaskName = new JTextField();
        txtTaskName.setBounds(40, 80, 370, 60);
        txtTaskName.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_GREEN, 2), "TÊN CÔNG VIỆC  (*)"));

        JComboBox<String> cbCat = new JComboBox<>(new String[]{"Công việc", "Học tập", "Cá nhân", "Gia đình"});
        cbCat.setBounds(40, 160, 370, 55); cbCat.setBorder(BorderFactory.createTitledBorder("Danh mục"));

        JComboBox<String> cbPrio = new JComboBox<>(new String[]{"Cao", "Trung bình", "Thấp"});
        cbPrio.setBounds(40, 230, 180, 55); cbPrio.setBorder(BorderFactory.createTitledBorder("Ưu tiên"));

        JComboBox<String> cbStat = new JComboBox<>(new String[]{"Chưa làm", "Đang làm", "Hoàn thành"});
        cbStat.setBounds(230, 230, 180, 55); cbStat.setBorder(BorderFactory.createTitledBorder("Trạng thái"));

        JTextField txtDate = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtDate.setBounds(40, 300, 370, 55); txtDate.setBorder(BorderFactory.createTitledBorder("Hạn chót"));

        JButton btnSave = createRoundedButton("LƯU CÔNG VIỆC",PRIMARY_GREEN, Color.WHITE);
        btnSave.setBounds(40, 380, 370, 60);
        
        btnSave.addActionListener(e -> {
            if (!txtTaskName.getText().trim().isEmpty()) {
                masterTaskList.add(new Task(txtTaskName.getText(), cbCat.getSelectedItem().toString(), 
                    cbPrio.getSelectedItem().toString(), cbStat.getSelectedItem().toString(), txtDate.getText()));
                txtTaskName.setText(""); refreshHomeList(); cardLayout.show(mainContentPanel, "HOME");
            }
        });

        panel.add(lblTitle); panel.add(txtTaskName); panel.add(cbCat);
        panel.add(cbPrio); panel.add(cbStat); panel.add(txtDate); panel.add(btnSave);
        return panel;
    }

    // --- 4. MÀN HÌNH CHỈNH SỬA ---
    private JPanel createEditPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel("CHỈNH SỬA CÔNG VIỆC", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE); lblTitle.setForeground(PRIMARY_BLUE);
        lblTitle.setBounds(0, 30, 450, 30);

        txtEditTaskName = new JTextField();
        txtEditTaskName.setBounds(40, 80, 370, 60);
        txtEditTaskName.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2), "TÊN CÔNG VIỆC"));

        cbEditCategory = new JComboBox<>(new String[]{"Công việc", "Học tập", "Cá nhân", "Gia đình"});
        cbEditCategory.setBounds(40, 160, 370, 55); cbEditCategory.setBorder(BorderFactory.createTitledBorder("Danh mục"));

        cbEditPriority = new JComboBox<>(new String[]{"Cao", "Trung bình", "Thấp"});
        cbEditPriority.setBounds(40, 230, 180, 55); cbEditPriority.setBorder(BorderFactory.createTitledBorder("Ưu tiên"));

        cbEditStatus = new JComboBox<>(new String[]{"Chưa làm", "Đang làm", "Hoàn thành"});
        cbEditStatus.setBounds(230, 230, 180, 55); cbEditStatus.setBorder(BorderFactory.createTitledBorder("Trạng thái"));

        txtEditDate = new JTextField();
        txtEditDate.setBounds(40, 300, 370, 55); txtEditDate.setBorder(BorderFactory.createTitledBorder("Hạn chót"));

        JButton btnUpdate = createRoundedButton("CẬP NHẬT THAY ĐỔI", PRIMARY_BLUE, Color.WHITE);
        btnUpdate.setBounds(40, 380, 370, 60);
        
        btnUpdate.addActionListener(e -> {
            if (taskToEdit != null && !txtEditTaskName.getText().trim().isEmpty()) {
                taskToEdit.title = txtEditTaskName.getText();
                taskToEdit.category = cbEditCategory.getSelectedItem().toString();
                taskToEdit.priority = cbEditPriority.getSelectedItem().toString();
                taskToEdit.status = cbEditStatus.getSelectedItem().toString();
                taskToEdit.deadline = txtEditDate.getText();
                refreshHomeList(); cardLayout.show(mainContentPanel, "HOME");
            }
        });

        JButton btnCancel = createRoundedButton("HỦY BỎ", Color.WHITE, DELETE_RED);
        btnCancel.setBounds(40, 455, 370, 45);
        btnCancel.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));

        panel.add(lblTitle); panel.add(txtEditTaskName); panel.add(cbEditCategory);
        panel.add(cbEditPriority); panel.add(cbEditStatus); panel.add(txtEditDate); 
        panel.add(btnUpdate); panel.add(btnCancel);
        return panel;
    }

    // --- 5. MÀN HÌNH BÁO CÁO ---
    private JPanel createReportPanel() {
        reportContainerPanel = new JPanel(null); 
        reportContainerPanel.setBackground(BG_COLOR);
        buildReportUI(); 
        return reportContainerPanel;
    }

   private void buildReportUI() {
        reportContainerPanel.removeAll();

        JLabel title = new JLabel("BÁO CÁO & THỐNG KÊ", SwingConstants.CENTER);
        title.setFont(FONT_TITLE); title.setForeground(TEXT_DARK);
        title.setBounds(0, 30, 450, 40);

        int completed = 0, doing = 0, notDone = 0;
        for (Task t : masterTaskList) {
            switch (t.status) {
                case "Hoàn thành": completed++; break;
                case "Đang làm": doing++; break;
                case "Chưa làm": notDone++; break;
            }
        }
        int total = completed + doing + notDone;
        int percent = (total == 0) ? 0 : (int)((completed * 100.0) / total);

        // --- PHẦN 1: STATUS (Biểu đồ tròn) ---
        JPanel statusPanel = new JPanel(null);
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBounds(20, 90, 400, 250); 
        statusPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        JLabel lblStatus = new JLabel("Trạng thái (Status)");
        lblStatus.setFont(FONT_BOLD); lblStatus.setBounds(15, 10, 200, 20);
        
        CircularProgressBar circleChart = new CircularProgressBar(percent);
        circleChart.setBounds(125, 40, 150, 150);
        
        JLabel note = new JLabel("Đã hoàn thành: " + completed + "/" + total);
        note.setHorizontalAlignment(SwingConstants.CENTER);
        note.setBounds(0, 200, 400, 30); note.setForeground(Color.GRAY);

        statusPanel.add(lblStatus); statusPanel.add(circleChart); statusPanel.add(note);

        // --- PHẦN 2: REPORT (Biểu đồ cột) ---
        JPanel reportChartPanel = new JPanel(null);
        reportChartPanel.setBackground(Color.WHITE);
        reportChartPanel.setBounds(20, 360, 400, 250); 
        reportChartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        JLabel lblReport = new JLabel("Biểu đồ (Report)");
        lblReport.setFont(FONT_BOLD); lblReport.setBounds(15, 10, 200, 20);

        BarChart barChart = new BarChart(completed, doing, notDone);
        barChart.setBounds(30, 50, 340, 180);

        reportChartPanel.add(lblReport); reportChartPanel.add(barChart);

        reportContainerPanel.add(title);
        reportContainerPanel.add(statusPanel);
        reportContainerPanel.add(reportChartPanel);

        reportContainerPanel.revalidate();
        reportContainerPanel.repaint();
    }

    private void refreshReportPanel() { buildReportUI(); }

    // --- 6. PROFILE & NAV ---
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_COLOR);
        lblWelcomeProfile = new JLabel("XIN CHÀO!", SwingConstants.CENTER);
        lblWelcomeProfile.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblWelcomeProfile.setBounds(0, 100, 450, 40);
        
        JButton btnLogout = createRoundedButton("ĐĂNG XUẤT", DELETE_RED, Color.WHITE);
        btnLogout.setBounds(100, 250, 250, 60);
        btnLogout.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "LOGIN");
            remove(((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.SOUTH));
        });

        panel.add(lblWelcomeProfile); panel.add(btnLogout);
        return panel;
    }

   private JPanel createBottomNav() {
    JPanel nav = new JPanel(new GridLayout(1, 4));
    
    nav.setPreferredSize(new Dimension(450, 80)); 
    nav.setBackground(Color.WHITE);
    nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

    String[][] menus = {{"🏠", "HOME"}, {"➕", "ADD"}, {"📊", "REPORT"}, {"👤", "PROFILE"}};
    
    for (String[] m : menus) {
        JButton b = new JButton("<html><center><nobr><font size='5'>" + m[0] + "</font></nobr><br><nobr><font size='3'><b>" + m[1] + "</b></font></nobr></center></html>");
        
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setBorderPainted(false); 
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setVerticalAlignment(SwingConstants.CENTER); 
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Font chữ màu xám nhẹ cho hiện đại 
        b.setForeground(new Color(100, 100, 100)); 

        b.addActionListener(e -> {
            if (m[1].equals("REPORT")) refreshReportPanel();
            if (m[1].equals("HOME")) refreshHomeList();
            cardLayout.show(mainContentPanel, m[1]);
        });
        
        nav.add(b);
    }
    return nav;
}
    // --- HELPER METHODS ---

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createSmallIconButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(40, 30));
        b.setMargin(new Insets(2, 2, 2, 2));
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        return b;
    }

    class Task {
        String title, category, priority, status, deadline;
        public Task(String t, String c, String p, String s, String d) {
            title = t; category = c; priority = p; status = s; deadline = d;
        }
    }

    private Color getPriorityColor(String p) {
        if (p.equals("Cao")) return DELETE_RED;
        if (p.equals("Trung bình")) return WARNING_ORANGE;
        return SUCCESS_GREEN;
    }

    private Color getStatusColor(String s) {
        if (s.equals("Hoàn thành")) return SUCCESS_GREEN;
        if (s.equals("Đang làm")) return PRIMARY_COLOR;
        return Color.GRAY;
    }

    private JLabel createBadge(String text, Color color) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setOpaque(true); lbl.setBackground(color);
        lbl.setForeground(TEXT_DARK);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        return lbl;
    }

    // --- BIỂU ĐỒ ---
    class CircularProgressBar extends JPanel {
        private int progress; 
        public CircularProgressBar(int progress) { this.progress = progress; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(223, 228, 234)); g2.drawOval(10, 10, 130, 130);
            g2.setColor(SUCCESS_GREEN);
            g2.drawArc(10, 10, 130, 130, 90, -(int) (360 * (progress / 100.0)));
            String text = progress + "%"; 
            g2.setFont(new Font("SansSerif", Font.BOLD, 30)); g2.setColor(TEXT_DARK);
            g2.drawString(text, 75 - g2.getFontMetrics().stringWidth(text)/2, 85);
        }
    }

    class BarChart extends JPanel {
        int v1, v2, v3;
        public BarChart(int c, int d, int n) { v1=c; v2=d; v3=n; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = Math.max(v1, Math.max(v2, v3)); if (max == 0) max = 1;
            int h = 120;
            drawBar(g2, 30, h, v1, max, SUCCESS_GREEN, "Xong");
            drawBar(g2, 130, h, v2, max, PRIMARY_COLOR, "Đang làm");
            drawBar(g2, 230, h, v3, max, DELETE_RED, "Chưa");
        }
        private void drawBar(Graphics2D g, int x, int maxH, int val, int maxVal, Color c, String label) {
            int barH = (int)((val / (double)maxVal) * maxH);
            if(barH < 5 && val > 0) barH = 5;
            g.setColor(c); g.fillRoundRect(x, maxH - barH + 20, 50, barH, 10, 10);
            g.setColor(Color.GRAY); g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString(String.valueOf(val), x + 20, maxH - barH + 15);
            g.drawString(label, x + 5, maxH + 40);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}