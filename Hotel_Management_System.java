import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Hotel_Management_System extends JFrame implements ActionListener {


    JTextField tfName, tfMobile, tfRoom, tfDays, tfSearch;
    JComboBox<String> cbType;
    JButton btnBook, btnDelete, btnSearch, btnClear;
    JTable table;
    DefaultTableModel model;
    Connection con;

    Hotel_Management_System() {

        setTitle("Hotel Management System");
        setSize(900, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectDatabase();

        JLabel title = new JLabel("HOTEL MANAGEMENT SYSTEM");
        title.setBounds(250, 20, 400, 30);
        add(title);

        JLabel l1 = new JLabel("Name");
        l1.setBounds(30, 80, 100, 25);
        add(l1);

        JLabel l2 = new JLabel("Mobile");
        l2.setBounds(30, 120, 100, 25);
        add(l2);

        JLabel l3 = new JLabel("Room No");
        l3.setBounds(30, 160, 100, 25);
        add(l3);

        JLabel l4 = new JLabel("Days");
        l4.setBounds(30, 200, 100, 25);
        add(l4);

        JLabel l5 = new JLabel("Room Type");
        l5.setBounds(30, 240, 100, 25);
        add(l5);

        tfName = new JTextField();
        tfName.setBounds(140, 80, 180, 25);
        add(tfName);

        tfMobile = new JTextField();
        tfMobile.setBounds(140, 120, 180, 25);
        add(tfMobile);

        tfRoom = new JTextField();
        tfRoom.setBounds(140, 160, 180, 25);
        add(tfRoom);

        tfDays = new JTextField();
        tfDays.setBounds(140, 200, 180, 25);
        add(tfDays);

        cbType = new JComboBox<>(new String[]{
                "AC", "NON-AC", "DELUXE", "SUITE"
        });
        cbType.setBounds(140, 240, 180, 25);
        add(cbType);

        btnBook = new JButton("BOOK");
        btnBook.setBounds(20, 300, 90, 30);
        btnBook.addActionListener(this);
        add(btnBook);

        btnDelete = new JButton("DELETE");
        btnDelete.setBounds(120, 300, 90, 30);
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnClear = new JButton("CLEAR");
        btnClear.setBounds(220, 300, 90, 30);
        btnClear.addActionListener(this);
        add(btnClear);

        JLabel searchLabel = new JLabel("Search Mobile");
        searchLabel.setBounds(420, 80, 100, 25);
        add(searchLabel);

        tfSearch = new JTextField();
        tfSearch.setBounds(530, 80, 150, 25);
        add(tfSearch);

        btnSearch = new JButton("SEARCH");
        btnSearch.setBounds(700, 80, 100, 25);
        btnSearch.addActionListener(this);
        add(btnSearch);

        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Mobile");
        model.addColumn("Room");
        model.addColumn("Days");
        model.addColumn("Type");

        table = new JTable(model);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(370, 130, 500, 350);
        add(sp);

        loadData();

        setVisible(true);
    }

    // Database Connection
    public void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hotel_db",
                    "root",
                    "Kaurav@123"
            );

            System.out.println("Database Connected");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    // Load Data
    public void loadData() {
        try {

            model.setRowCount(0);

            PreparedStatement ps =
                    con.prepareStatement("SELECT * FROM hotel");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("mobile"),
                        rs.getString("room"),
                        rs.getInt("days"),
                        rs.getString("type")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    public void actionPerformed(ActionEvent ae) {

        // BOOK
        if (ae.getSource() == btnBook) {

            try {

                String query =
                        "INSERT INTO hotel(name,mobile,room,days,type) VALUES(?,?,?,?,?)";

                PreparedStatement ps =
                        con.prepareStatement(query);

                ps.setString(1, tfName.getText());
                ps.setString(2, tfMobile.getText());
                ps.setString(3, tfRoom.getText());
                ps.setInt(4, Integer.parseInt(tfDays.getText()));
                ps.setString(5, cbType.getSelectedItem().toString());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Room Booked Successfully");

                loadData();
                clearFields();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

        // DELETE
        if (ae.getSource() == btnDelete) {

            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Select a record");
                return;
            }

            try {

                int id = (int) model.getValueAt(row, 0);

                PreparedStatement ps =
                        con.prepareStatement(
                                "DELETE FROM hotel WHERE id=?");

                ps.setInt(1, id);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Record Deleted");

                loadData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

        // SEARCH
        if (ae.getSource() == btnSearch) {

            try {

                model.setRowCount(0);

                PreparedStatement ps =
                        con.prepareStatement(
                                "SELECT * FROM hotel WHERE mobile=?");

                ps.setString(1, tfSearch.getText());

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("mobile"),
                            rs.getString("room"),
                            rs.getInt("days"),
                            rs.getString("type")
                    });
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

        // CLEAR
        if (ae.getSource() == btnClear) {
            clearFields();
            loadData();
        }
    }

    public void clearFields() {
        tfName.setText("");
        tfMobile.setText("");
        tfRoom.setText("");
        tfDays.setText("");
        tfSearch.setText("");
    }

    public static void main(String[] args) {
        new Hotel_Management_System();
    }
}