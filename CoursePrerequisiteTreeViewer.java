import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CoursePrerequisiteTreeViewer extends JFrame {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTextField courseField, prerequisiteField;
    private Map<String, DefaultMutableTreeNode> courseNodes;
    
    // Beautiful color palette
    private static final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private static final Color SECONDARY_COLOR = new Color(80, 200, 120);
    private static final Color ACCENT_COLOR = new Color(255, 107, 107);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(45, 55, 72);
    
    public CoursePrerequisiteTreeViewer() {
        courseNodes = new HashMap<>();
        initializeGUI();
        setupSampleData();
    }
    
    private void initializeGUI() {
        setTitle("Course Prerequisite Tree Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main panels
        JPanel mainPanel = createMainPanel();
        JPanel controlPanel = createControlPanel();
        
        add(mainPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Apply modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Course Prerequisite Tree", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Tree setup
        rootNode = new DefaultMutableTreeNode("Computer Science Courses");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        
        customizeTree();
        
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(CARD_COLOR);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void customizeTree() {
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tree.setRowHeight(30);
        tree.setBackground(CARD_COLOR);
        tree.setForeground(TEXT_COLOR);
        
        // Custom cell renderer for colorful nodes
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String nodeText = node.toString();
                
                if (node.isRoot()) {
                    setIcon(createColoredIcon(PRIMARY_COLOR, 16));
                    setFont(getFont().deriveFont(Font.BOLD, 16f));
                } else if (node.isLeaf()) {
                    setIcon(createColoredIcon(ACCENT_COLOR, 12));
                    setFont(getFont().deriveFont(Font.PLAIN, 14f));
                } else {
                    setIcon(createColoredIcon(SECONDARY_COLOR, 14));
                    setFont(getFont().deriveFont(Font.BOLD, 14f));
                }
                
                if (selected) {
                    setBackgroundSelectionColor(new Color(PRIMARY_COLOR.getRed(), 
                        PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
                    setBorderSelectionColor(PRIMARY_COLOR);
                }
                
                return this;
            }
        });
        
        // Expand all nodes initially
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    private Icon createColoredIcon(Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(x, y, size, size);
                g2d.setColor(color.darker());
                g2d.drawOval(x, y, size, size);
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() { return size; }
            
            @Override
            public int getIconHeight() { return size; }
        };
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Course field
        JLabel courseLabel = createStyledLabel("Course:");
        courseField = createStyledTextField();
        
        // Prerequisite field
        JLabel prereqLabel = createStyledLabel("Prerequisite:");
        prerequisiteField = createStyledTextField();
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(courseLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(courseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(prereqLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(prerequisiteField, gbc);
        
        return panel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 224), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton addButton = createStyledButton("Add Prerequisite", PRIMARY_COLOR);
        JButton removeButton = createStyledButton("Remove Course", ACCENT_COLOR);
        JButton clearButton = createStyledButton("Clear All", SECONDARY_COLOR);
        
        addButton.addActionListener(e -> addPrerequisite());
        removeButton.addActionListener(e -> removeCourse());
        clearButton.addActionListener(e -> clearAll());
        
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void addPrerequisite() {
        String course = courseField.getText().trim();
        String prerequisite = prerequisiteField.getText().trim();
        
        if (course.isEmpty()) {
            showMessage("Please enter a course name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultMutableTreeNode courseNode = courseNodes.get(course);
        if (courseNode == null) {
            courseNode = new DefaultMutableTreeNode(course);
            rootNode.add(courseNode);
            courseNodes.put(course, courseNode);
        }
        
        if (!prerequisite.isEmpty()) {
            DefaultMutableTreeNode prereqNode = courseNodes.get(prerequisite);
            if (prereqNode == null) {
                prereqNode = new DefaultMutableTreeNode(prerequisite);
                courseNodes.put(prerequisite, prereqNode);
            }
            
            if (!isNodeChild(courseNode, prereqNode)) {
                courseNode.add(new DefaultMutableTreeNode(prerequisite));
            }
        }
        
        treeModel.reload();
        expandAllNodes();
        courseField.setText("");
        prerequisiteField.setText("");
        
        showMessage("Prerequisite added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void removeCourse() {
        String course = courseField.getText().trim();
        if (course.isEmpty()) {
            showMessage("Please enter a course name to remove.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultMutableTreeNode nodeToRemove = courseNodes.get(course);
        if (nodeToRemove != null) {
            treeModel.removeNodeFromParent(nodeToRemove);
            courseNodes.remove(course);
            courseField.setText("");
            showMessage("Course removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Course not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearAll() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all courses?", 
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            rootNode.removeAllChildren();
            courseNodes.clear();
            treeModel.reload();
            courseField.setText("");
            prerequisiteField.setText("");
            setupSampleData();
        }
    }
    
    private boolean isNodeChild(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).toString().equals(child.toString())) {
                return true;
            }
        }
        return false;
    }
    
    private void expandAllNodes() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private void setupSampleData() {
        // Add sample courses with prerequisites
        String[][] sampleData = {
            {"Data Structures", "Programming Fundamentals"},
            {"Algorithms", "Data Structures"},
            {"Database Systems", "Data Structures"},
            {"Software Engineering", "Programming Fundamentals"},
            {"Computer Networks", "Operating Systems"},
            {"Operating Systems", "Data Structures"},
            {"Machine Learning", "Statistics"},
            {"Machine Learning", "Linear Algebra"},
            {"Artificial Intelligence", "Machine Learning"},
            {"Web Development", "Programming Fundamentals"},
            {"Mobile Development", "Programming Fundamentals"}
        };
        
        for (String[] data : sampleData) {
            String course = data[0];
            String prerequisite = data[1];
            
            DefaultMutableTreeNode courseNode = courseNodes.get(course);
            if (courseNode == null) {
                courseNode = new DefaultMutableTreeNode(course);
                rootNode.add(courseNode);
                courseNodes.put(course, courseNode);
            }
            
            DefaultMutableTreeNode prereqNode = new DefaultMutableTreeNode(prerequisite);
            courseNode.add(prereqNode);
            
            if (!courseNodes.containsKey(prerequisite)) {
                courseNodes.put(prerequisite, prereqNode);
            }
        }
        
        treeModel.reload();
        expandAllNodes();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new CoursePrerequisiteTreeViewer().setVisible(true);
        });
    }
}