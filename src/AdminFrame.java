import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminFrame extends JFrame {
    private Connection conn;

    public AdminFrame(Connection conn) {
        this.conn = conn;
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JButton initDbButton = new JButton("Initialize Database");
        initDbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeDatabase();
            }
        });
        panel.add(initDbButton);

        JButton viewTablesButton = new JButton("View All Tables");
        viewTablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAllTables();
            }
        });
        panel.add(viewTablesButton);

        JButton insertButton = new JButton("Insert Data");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });
        panel.add(insertButton);

        JButton deleteOrUpdateButton = new JButton("Delete/Update Data");
        deleteOrUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrUpdateData();
            }
        });
        panel.add(deleteOrUpdateButton);

        add(panel);
    }

    private void initializeDatabase() {
        String[] initQueries = {
                "DROP TABLE IF EXISTS `좌석`;",
                "DROP TABLE IF EXISTS `티켓`;",
                "DROP TABLE IF EXISTS `예매`;",
                "DROP TABLE IF EXISTS `회원`;",
                "DROP TABLE IF EXISTS `상영일정`;",
                "DROP TABLE IF EXISTS `상영관`;",
                "DROP TABLE IF EXISTS `영화`;",
                // 영화 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`영화` (" +
                        "`영화번호` INT NOT NULL," +
                        "`영화명` VARCHAR(255) NOT NULL," +
                        "`상영시간` INT NOT NULL," +
                        "`상영등급` VARCHAR(50) NOT NULL," +
                        "`감독명` VARCHAR(100) NOT NULL," +
                        "`배우명` TEXT NOT NULL," +
                        "`장르` VARCHAR(100) NOT NULL," +
                        "`영화소개` TEXT NOT NULL," +
                        "`개봉일자` DATE NULL," +
                        "`평점` FLOAT NULL," +
                        "`썸네일경로` VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (`영화번호`)) ENGINE = InnoDB;",
                // 상영관 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`상영관` (" +
                        "`상영관번호` INT NOT NULL," +
                        "`좌석수` INT NOT NULL," +
                        "`상영관사용여부` TINYINT NOT NULL," +
                        "PRIMARY KEY (`상영관번호`)) ENGINE = InnoDB;",
                // 상영일정 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`상영일정` (" +
                        "`상영일정번호` INT NOT NULL," +
                        "`상영시작일` DATE NOT NULL," +
                        "`상영요일` VARCHAR(10) NOT NULL," +
                        "`상영회차` INT NOT NULL," +
                        "`상영시작시간` TIME NOT NULL," +
                        "`상영관번호` INT NOT NULL," +
                        "`영화번호` INT NOT NULL," +
                        "PRIMARY KEY (`상영일정번호`)," +
                        "INDEX `fk_상영일정_상영관_idx` (`상영관번호` ASC) VISIBLE," +
                        "INDEX `fk_상영일정_영화1_idx` (`영화번호` ASC) VISIBLE," +
                        "CONSTRAINT `fk_상영일정_상영관`" +
                        "FOREIGN KEY (`상영관번호`)" +
                        "REFERENCES `db1`.`상영관` (`상영관번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION," +
                        "CONSTRAINT `fk_상영일정_영화1`" +
                        "FOREIGN KEY (`영화번호`)" +
                        "REFERENCES `db1`.`영화` (`영화번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION) ENGINE = InnoDB;",
                // 좌석 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`좌석` (" +
                        "`좌석번호` INT NOT NULL," +
                        "`좌석사용여부` TINYINT NOT NULL," +
                        "`상영관번호` INT NOT NULL," +
                        "PRIMARY KEY (`좌석번호`)," +
                        "INDEX `fk_좌석_상영관1_idx` (`상영관번호` ASC) VISIBLE," +
                        "CONSTRAINT `fk_좌석_상영관1`" +
                        "FOREIGN KEY (`상영관번호`)" +
                        "REFERENCES `db1`.`상영관` (`상영관번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION) ENGINE = InnoDB;",
                // 회원 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`회원` (" +
                        "`회원번호` INT NOT NULL," +
                        "`회원아이디` VARCHAR(50) NOT NULL," +
                        "`고객명` VARCHAR(100) NOT NULL," +
                        "`휴대폰번호` VARCHAR(20) NOT NULL," +
                        "`전자메일주소` VARCHAR(255) NULL," +
                        "PRIMARY KEY (`회원번호`)) ENGINE = InnoDB;",
                // 예매 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`예매` (" +
                        "`예매번호` INT NOT NULL," +
                        "`결제방법` VARCHAR(50) NOT NULL," +
                        "`결제상태` VARCHAR(20) NOT NULL," +
                        "`결제금액` DECIMAL(10,2) NOT NULL," +
                        "`결제일자` DATE NOT NULL," +
                        "`회원번호` INT NOT NULL," +
                        "PRIMARY KEY (`예매번호`)," +
                        "INDEX `fk_예매_회원1_idx` (`회원번호` ASC) VISIBLE," +
                        "CONSTRAINT `fk_예매_회원1`" +
                        "FOREIGN KEY (`회원번호`)" +
                        "REFERENCES `db1`.`회원` (`회원번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION) ENGINE = InnoDB;",
                // 티켓 테이블
                "CREATE TABLE IF NOT EXISTS `db1`.`티켓` (" +
                        "`티켓번호` INT NOT NULL," +
                        "`발권여부` TINYINT NOT NULL," +
                        "`표준가격` DECIMAL(10,2) NOT NULL," +
                        "`판매가격` DECIMAL(10,2) NOT NULL," +
                        "`예매번호` INT NOT NULL," +
                        "`상영일정번호` INT NOT NULL," +
                        "`상영관번호` INT NOT NULL," +
                        "`좌석번호` INT NOT NULL," +
                        "PRIMARY KEY (`티켓번호`)," +
                        "INDEX `fk_티켓_예매1_idx` (`예매번호` ASC) VISIBLE," +
                        "INDEX `fk_티켓_상영일정1_idx` (`상영일정번호` ASC) VISIBLE," +
                        "INDEX `fk_티켓_상영관1_idx` (`상영관번호` ASC) VISIBLE," +
                        "INDEX `fk_티켓_좌석1_idx` (`좌석번호` ASC) VISIBLE," +
                        "CONSTRAINT `fk_티켓_예매1`" +
                        "FOREIGN KEY (`예매번호`)" +
                        "REFERENCES `db1`.`예매` (`예매번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION," +
                        "CONSTRAINT `fk_티켓_상영일정1`" +
                        "FOREIGN KEY (`상영일정번호`)" +
                        "REFERENCES `db1`.`상영일정` (`상영일정번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION," +
                        "CONSTRAINT `fk_티켓_상영관1`" +
                        "FOREIGN KEY (`상영관번호`)" +
                        "REFERENCES `db1`.`상영관` (`상영관번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION," +
                        "CONSTRAINT `fk_티켓_좌석1`" +
                        "FOREIGN KEY (`좌석번호`)" +
                        "REFERENCES `db1`.`좌석` (`좌석번호`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION) ENGINE = InnoDB;"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String query : initQueries) {
                stmt.executeUpdate(query);
            }
            JOptionPane.showMessageDialog(this, "Database initialized successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database initialization failed");
        }
    }

    private void viewAllTables() {
    	try (Statement stmt = conn.createStatement()) {
            String[] tableNames = {"영화", "상영관", "상영일정", "좌석", "회원", "예매", "티켓"};
            StringBuilder result = new StringBuilder();

            for (String tableName : tableNames) {
                result.append("Table: ").append(tableName).append("\n");
                var rs = stmt.executeQuery("SELECT * FROM " + tableName);
                var metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(metaData.getColumnName(i)).append(": ").append(rs.getString(i)).append(" ");
                    }
                    result.append("\n");
                }
                result.append("\n");
            }

            JTextArea textArea = new JTextArea(result.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            JFrame frame = new JFrame("All Tables");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(500, 400);
            frame.add(scrollPane);
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve tables");
        }
    }

    private void insertData() {
        JFrame insertFrame = new JFrame("Insert Data");
        insertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertFrame.setSize(400, 400);
        insertFrame.setLayout(new GridLayout(10, 2));

        JTextField 영화번호Field = new JTextField();
        JTextField 영화명Field = new JTextField();
        JTextField 상영시간Field = new JTextField();
        JTextField 상영등급Field = new JTextField();
        JTextField 감독명Field = new JTextField();
        JTextField 배우명Field = new JTextField();
        JTextField 장르Field = new JTextField();
        JTextField 영화소개Field = new JTextField();
        JTextField 개봉일자Field = new JTextField();
        JTextField 평점Field = new JTextField();
        JTextField 썸네일경로Field = new JTextField();

        insertFrame.add(new JLabel("영화번호:"));
        insertFrame.add(영화번호Field);
        insertFrame.add(new JLabel("영화명:"));
        insertFrame.add(영화명Field);
        insertFrame.add(new JLabel("상영시간:"));
        insertFrame.add(상영시간Field);
        insertFrame.add(new JLabel("상영등급:"));
        insertFrame.add(상영등급Field);
        insertFrame.add(new JLabel("감독명:"));
        insertFrame.add(감독명Field);
        insertFrame.add(new JLabel("배우명:"));
        insertFrame.add(배우명Field);
        insertFrame.add(new JLabel("장르:"));
        insertFrame.add(장르Field);
        insertFrame.add(new JLabel("영화소개:"));
        insertFrame.add(영화소개Field);
        insertFrame.add(new JLabel("개봉일자:"));
        insertFrame.add(개봉일자Field);
        insertFrame.add(new JLabel("평점:"));
        insertFrame.add(평점Field);
        insertFrame.add(new JLabel("썸네일경로:"));
        insertFrame.add(썸네일경로Field);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String 영화번호 = 영화번호Field.getText();
                String 영화명 = 영화명Field.getText();
                String 상영시간 = 상영시간Field.getText();
                String 상영등급 = 상영등급Field.getText();
                String 감독명 = 감독명Field.getText();
                String 배우명 = 배우명Field.getText();
                String 장르 = 장르Field.getText();
                String 영화소개 = 영화소개Field.getText();
                String 개봉일자 = 개봉일자Field.getText();
                String 평점 = 평점Field.getText();
                String 썸네일경로 = 썸네일경로Field.getText();

                try (Statement stmt = conn.createStatement()) {
                    String query = "INSERT INTO `db1`.`영화` VALUES (" +
                            영화번호 + ", '" +
                            영화명 + "', " +
                            상영시간 + ", '" +
                            상영등급 + "', '" +
                            감독명 + "', '" +
                            배우명 + "', '" +
                            장르 + "', '" +
                            영화소개 + "', '" +
                            개봉일자 + "', " +
                            평점 + ", '" +
                            썸네일경로 + "')";
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(insertFrame, "Data inserted successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(insertFrame, "Failed to insert data");
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertFrame.dispose();
            }
        });

        insertFrame.add(saveButton);
        insertFrame.add(cancelButton);

        insertFrame.setVisible(true);
    }

    private void deleteOrUpdateData() {
        JFrame modifyFrame = new JFrame("Delete or Update Data");
        modifyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modifyFrame.setSize(400, 400);
        modifyFrame.setLayout(new GridLayout(3, 2));

        JTextField tableNameField = new JTextField();
        JTextField conditionField = new JTextField();
        JTextField updateField = new JTextField();

        modifyFrame.add(new JLabel("Table Name:"));
        modifyFrame.add(tableNameField);
        modifyFrame.add(new JLabel("Condition:"));
        modifyFrame.add(conditionField);
        modifyFrame.add(new JLabel("Update:"));
        modifyFrame.add(updateField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String condition = conditionField.getText();

                try (Statement stmt = conn.createStatement()) {
                    String query = "DELETE FROM " + tableName + " WHERE " + condition;
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(modifyFrame, "Data deleted successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(modifyFrame, "Failed to delete data");
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String condition = conditionField.getText();
                String update = updateField.getText();

                try (Statement stmt = conn.createStatement()) {
                    String query = "UPDATE " + tableName + " SET " + update + " WHERE " + condition;
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(modifyFrame, "Data updated successfully");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(modifyFrame, "Failed to update data");
                }
            }
        });

        modifyFrame.add(deleteButton);
        modifyFrame.add(updateButton);

        modifyFrame.setVisible(true);
    }

}
