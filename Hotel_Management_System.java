import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Hotel_Management_System extends JFrame {

    JTextField name, mobile, room, days;
    JComboBox<String> type;
    JTable table;
    DefaultTableModel model;
    Connection con;
    Image image;

    Hotel_Management_System() {

        setTitle("Hotel Management System");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background Image
        try {
            image = new ImageIcon(
                getClass().getResource("/Hotel.png")
            ).getImage();
        } catch (Exception e) {
            image = null;
        }

        // Background Panel
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (image != null) {
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        bg.setLayout(null);
        setContentPane(bg);

        // Labels
        JLabel l1 = new JLabel("Name");
        JLabel l2 = new JLabel("Mobile");
        JLabel l3 = new JLabel("Room No");
        JLabel l4 = new JLabel("Days");
        JLabel l5 = new JLabel("Type");

        l1.setBounds(20,20,80,25);
        l2.setBounds(20,60,80,25);
        l3.setBounds(20,100,80,25);
        l4.setBounds(20,140,80,25);
        l5.setBounds(20,180,80,25);

        bg.add(l1);
        bg.add(l2);
        bg.add(l3);
        bg.add(l4);
        bg.add(l5);

        // Fields
        name = new JTextField();
        mobile = new JTextField();
        room = new JTextField();
        days = new JTextField();

        name.setBounds(100,20,150,25);
        mobile.setBounds(100,60,150,25);
        room.setBounds(100,100,150,25);
        days.setBounds(100,140,150,25);

        bg.add(name);
        bg.add(mobile);
        bg.add(room);
        bg.add(days);

        // Type
        type = new JComboBox<>(
            new String[]{"AC","NON-AC"}
        );

        type.setBounds(100,180,150,25);
        bg.add(type);

        // Buttons
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");

        add.setBounds(10,220,80,30);
        update.setBounds(95,220,90,30);
        delete.setBounds(190,220,80,30);
        clear.setBounds(10,260,80,30);

        bg.add(add);
        bg.add(update);
        bg.add(delete);
        bg.add(clear);

        // Table
        model = new DefaultTableModel(
            new String[]{"ID","Name","Mobile","Room","Days","Type"}, 0
        );

        table = new JTable(model);
        table.setRowHeight(28);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(300,20,450,400);

        bg.add(sp);

        // Database
        connectDB();
        loadData();

        // Add
        add.addActionListener(e -> addRoom());

        // Update
        update.addActionListener(e -> updateRoom());

        // Delete
        delete.addActionListener(e -> deleteRoom());

        // Clear
        clear.addActionListener(e -> clear());

        // Table Click
        table.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent e) {

                int r = table.getSelectedRow();

                if (r >= 0) {

                    name.setText(model.getValueAt(r,1).toString());
                    mobile.setText(model.getValueAt(r,2).toString());
                    room.setText(model.getValueAt(r,3).toString());
                    days.setText(model.getValueAt(r,4).toString());

                    type.setSelectedItem(
                        model.getValueAt(r,5).toString()
                    );
                }
            }
        });

        setVisible(true);
    }

    // Database Connection
    void connectDB() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hotel",
                "root",
                "Kaurav@123"
            );

            System.out.println("Database Connected");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Database Error: " + e.getMessage()
            );
        }
    }

    // Load Data
    void loadData() {

        if (con == null) return;

        try {

            model.setRowCount(0);

            ResultSet rs = con.createStatement().executeQuery(
                "SELECT * FROM rooms"
            );

            while (rs.next()) {

                model.addRow(new Object[]{

                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("mobile"),
                    rs.getString("room_no"),
                    rs.getInt("days"),
                    rs.getString("type")
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    // Add Room
    void addRoom() {

        if (name.getText().isEmpty() ||
            mobile.getText().isEmpty() ||
            room.getText().isEmpty() ||
            days.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please fill all fields"
            );

            return;
        }

        try {

            int d = Integer.parseInt(days.getText());

            if (d <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Days must be greater than 0"
                );
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO rooms " +
                "(name,mobile,room_no,days,type) VALUES(?,?,?,?,?)"
            );

            ps.setString(1, name.getText());
            ps.setString(2, mobile.getText());
            ps.setString(3, room.getText());
            ps.setInt(4, d);
            ps.setString(5, type.getSelectedItem().toString());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Added Successfully"
            );

            loadData();
            clear();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error: " + e.getMessage()
            );
        }
    }

    // Update Room
    void updateRoom() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Select a record first"
            );

            return;
        }

        try {

            int id = Integer.parseInt(
                model.getValueAt(row,0).toString()
            );

            int d = Integer.parseInt(days.getText());

            PreparedStatement ps = con.prepareStatement(
                "UPDATE rooms SET name=?, mobile=?, room_no=?, " +
                "days=?, type=? WHERE id=?"
            );

            ps.setString(1, name.getText());
            ps.setString(2, mobile.getText());
            ps.setString(3, room.getText());
            ps.setInt(4, d);
            ps.setString(5, type.getSelectedItem().toString());
            ps.setInt(6, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Updated Successfully"
            );

            loadData();
            clear();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error: " + e.getMessage()
            );
        }
    }

    // Delete Room
    void deleteRoom() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Select a record first"
            );

            return;
        }

        try {

            int id = Integer.parseInt(
                model.getValueAt(row,0).toString()
            );

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM rooms WHERE id=?"
            );

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Room Deleted Successfully"
            );

            loadData();
            clear();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }

    // Clear
    void clear() {

        name.setText("");
        mobile.setText("");
        room.setText("");
        days.setText("");
        type.setSelectedIndex(0);

        table.clearSelection();
    }

    public static void main(String[] args) {
        new Hotel_Management_System();
    }
}
























































