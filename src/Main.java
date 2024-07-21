import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.awt.*;

public class Main {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/dataa";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        main_menu();
    }

    public static void main_menu() {
        JFrame frame = new JFrame("Главное меню");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel text = new JLabel("ГЛАВНОЕ МЕНЮ: ", SwingConstants.CENTER);
        text.setBounds(150, 10, 200, 30);

        JButton readers = new JButton("Поиск читателя");
        readers.setBounds(150, 50, 200, 30);

        JButton add_readers = new JButton("Добавить читателя");
        add_readers.setBounds(150, 85, 200, 30);

        JButton add_book = new JButton("Добавить книгу");
        add_book.setBounds(150, 120, 200, 30);

        JButton view_debtors = new JButton("Просмотр Должников");
        view_debtors.setBounds(150, 155, 200, 30);

        JButton exit = new JButton("Выход");
        exit.setBounds(150, 190, 200, 30);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();  //закрытие окно
            }
        });

        readers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readers();
            }
        });

        add_readers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add_readers();
            }
        });

        view_debtors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view_debtors();
            }
        });

        add_book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add_book();
            }
        });

        frame.add(text);
        frame.add(readers);
        frame.add(add_readers);
        frame.add(add_book);
        frame.add(view_debtors);
        frame.add(exit);
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        frame.setPreferredSize(new Dimension(500, 500));
        frame.getContentPane().setBackground(Color.gray);
        frame.pack();
        frame.setVisible(true);
    }

    public static void readers() {
        JFrame frame = new JFrame("Поиск читателя");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); //окно открыветься по центру экрана

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel); //Размещаем компоненты в JPanel
        frame.setVisible(true); //окно должень быт видимом
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null); // Устанавливаем свободное расположение компонентов

        JLabel titleLabel = new JLabel("ПОИСК ЧИТАТЕЛЯ", SwingConstants.CENTER);
        titleLabel.setBounds(200, 10, 200, 30);
        panel.add(titleLabel); // Добавляем метку в JPanel

        JLabel idLabel = new JLabel("Введите ID:");
        idLabel.setBounds(50, 50, 100, 20);
        panel.add(idLabel);

        JTextField idTextField = new JTextField();
        idTextField.setBounds(150, 50, 120, 20);
        panel.add(idTextField); // Добавляем поле для ввода ID в JPanel

        JButton searchButton = new JButton("Поиск");
        searchButton.setBounds(300, 50, 80, 20);
        panel.add(searchButton);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.setBounds(400, 50, 80, 20);
        panel.add(cancelButton); // Добавляем кнопку "Отмена" в JPanel

        // таблица для отображения результатов
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 500, 250); // Располагаем JScrollPane для таблицы
        panel.add(scrollPane); // Добавляем JScrollPane в JPanel

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idTextField.getText(); //получаем id
                fetchAndDisplayData(id, table); //выполняем запрос и отображаем результаты в таблице
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                topFrame.dispose(); //закрываем текущее окно
            }
        });
    }

    private static void fetchAndDisplayData(String id, JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("name");
        model.addColumn("email");
        model.addColumn("phone");

        String query = "SELECT * FROM data WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] rowData = {
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                };
                model.addRow(rowData);
            }
            table.setModel(model); //установка модель таблицы с результатами
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при выполнении запроса");
        }
    }

    public static void saveReadersToFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM readers");
             FileWriter fileWriter = new FileWriter("database.txt")) {

            fileWriter.write(String.format("%-10s%-20s%-30s%-15s\n", "ID", "Name", "Email", "Phone"));
            fileWriter.write("---------------------------------------------------------------\n");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                String formattedLine = String.format("%-10d%-20s%-30s%-15s\n", id, name, email, phone);
                fileWriter.write(formattedLine);
            }

            fileWriter.flush();
            System.out.println("Данные успешно сохранены в файл database.txt");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void add_readers() {
        JFrame frame = new JFrame("Добавить читателя");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel nameLabel = new JLabel("name: ");
        nameLabel.setBounds(50, 50, 100, 20);
        JTextField nameField = new JTextField(20);
        nameField.setBounds(150, 50, 200, 20);

        JLabel emailLabel = new JLabel("email: ");
        emailLabel.setBounds(50, 80, 100, 20);
        JTextField emailField = new JTextField(20);
        emailField.setBounds(150, 80, 200, 20);

        JLabel phoneLabel = new JLabel("phone: ");
        phoneLabel.setBounds(50, 110, 100, 20);
        JTextField phoneText = new JTextField(20);
        phoneText.setBounds(150, 110, 200, 20);

        JButton submitButton = new JButton("Добавить");
        submitButton.setBounds(150, 150, 200, 30);

        JButton exit_button = new JButton("Отмена");
        exit_button.setBounds(150, 185, 200, 30);

        exit_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneText.getText();
                addReaderToDatabase(name, email, phone);
                saveReadersToFile();
                frame.dispose();
            }
        });

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(submitButton);
        frame.add(exit_button);
        frame.add(phoneLabel);
        frame.add(phoneText);

        frame.setSize(500, 300);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void add_book() {
        JFrame g = new JFrame("Добавить книгу");
        JLabel l1 = new JLabel("Book Name");
        l1.setBounds(30, 15, 100, 30);

        JLabel l2 = new JLabel("Genre");
        l2.setBounds(30, 53, 100, 30);

        JLabel l3 = new JLabel("Price");
        l3.setBounds(30, 90, 100, 30);

        JTextField F_name = new JTextField();
        F_name.setBounds(110, 15, 200, 30);

        JTextField F_genre = new JTextField();
        F_genre.setBounds(110, 53, 200, 30);

        JTextField F_price = new JTextField();
        F_price.setBounds(110, 90, 200, 30);

        JButton create_but = new JButton("Добавить");
        create_but.setBounds(130, 130, 100, 25);
        create_but.addActionListener(e -> {
            String name = F_name.getText();
            String genre = F_genre.getText();
            String price = F_price.getText();

            addBookToDatabase(name, genre, price);
            saveBooksToFile();
            g.dispose();
        });

        g.add(l3);
        g.add(create_but);
        g.add(l1);
        g.add(l2);
        g.add(F_name);
        g.add(F_genre);
        g.add(F_price);
        g.setSize(350, 200);
        g.setLayout(null);
        g.setVisible(true);
        g.setLocationRelativeTo(null);
    }

    public static void addBookToDatabase(String name, String genre, String price) {
        String query = "INSERT INTO books (name, genre, price) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, genre);
            pstmt.setString(3, price);
            pstmt.executeUpdate();
            System.out.println("Книга успешно добавлена в базу данных");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "\nшибка при добавлении книги.");
        }
    }

    public static void saveBooksToFile() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books");
             FileWriter fileWriter = new FileWriter("books.txt")) {

            fileWriter.write(String.format("%-10s%-20s%-30s%-15s\n", "ID", "Name", "Genre", "Price"));
            fileWriter.write("------------------------------------------------------------------\n");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String genre = rs.getString("genre");
                String price = rs.getString("price");

                String formattedLine = String.format("%-10d%-20s%-30s%-15s\n", id, name, genre, price);
                fileWriter.write(formattedLine);
            }

            fileWriter.flush();
            System.out.println("данные успешно сохранены в файл books.txt");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void view_debtors() {
        JFrame frame = new JFrame("Просмотр Должников");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
//        frame.setLocationRelativeTo(null);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM data";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            //извлечение данных из ResultSet и создание таблицы
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setSize(500, 200);
            frame.setVisible(true);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void addReaderToDatabase(String name, String email, String phone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO readers(name, email, phone) VALUES(?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.executeUpdate();
                System.out.println("Читатель успешно добавлен в базу данных");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при добавлении читателя в базу данных");
        }
    }
}
