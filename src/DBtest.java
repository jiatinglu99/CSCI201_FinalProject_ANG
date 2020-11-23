import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Yunhan Mao at 2020/11/23
 * @using IntelliJ IDEA
 * A few test cases for sql database
 *
 * Running the test:
 * Option 1: run generalTest(), which tests functionalities of register,
 * add score, log in, and look up score.
 *
 * Option 2: run these tests separately.
 * Before all other tests are run, testRegister should be run first, to
 * create an entry. Then other tests could be run in any order
 *
 * Note: tests are meant to be run once with the preset value.
 * Or else it will fail because the entries become duplicate.
 */
public class DBtest {
    private static Connection connection;
    private static String username = "gary";
    private static String pwd = "001127";

    public DBtest() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("jdbcTest.properties"));
            Class.forName(properties.getProperty("classURL"));
            connection = DriverManager.getConnection((String) properties.get("jdbcURL"), properties.getProperty("username"), properties.getProperty("password"));
        } catch (Exception c) {
            c.printStackTrace();
        }
    }

    @Test
    public void generalTest(){
        String user="Yunhan";
        String password="Yunhan";
        int scoreToAdd=10;
        assertEquals(true,register(user,password));
        assertEquals(true,login(user,password));
        assertEquals(true,addScore(user,scoreToAdd));
        assertNotNull(lookUpScore(user));
    }


    @Test
    public void testRegister() {
        Boolean register = register(username, pwd);
        assertEquals(true, register);
    }

    public Boolean register(String us, String pd) {
        String sql1 = "insert into register(username,password,score) values(?,?,?);";
        PreparedStatement preStmt = null;
        String username = us;
        String pwd = pd;
        int score = 0;
        try {
            preStmt = connection.prepareStatement(sql1);
            preStmt.setString(1, username);
            preStmt.setString(2, pwd);
            preStmt.setInt(3, score);
            int i = -1;
            i = preStmt.executeUpdate();
            if (i >= 1) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Test
    public void testLogin() {
        Boolean login = login(username, pwd);
        assertEquals(true, login);
    }

    public Boolean login(String us, String pd) {
        String sql2 = "select * from register where username=? and password=?;";
        try {
            PreparedStatement sta = connection.prepareStatement(sql2);
            String username = us;
            String pwd = pd;
            sta.setString(1, username);
            sta.setString(2, pwd);
            ResultSet rs = sta.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Test
    public void testAddScore() {
        Boolean add = addScore(username, 20);
        assertEquals(true, add);
    }

    public Boolean addScore(String us, int score) {
        String sql3 = "update register SET score=score+? where username=?";
        int scoreToAdd = score;
        try {
            PreparedStatement sta = connection.prepareStatement(sql3);
            sta.setInt(1, scoreToAdd);
            sta.setString(2, us);
            int j = -1;
            j = sta.executeUpdate();
            if (j >= 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Test
    public void testLookUpScore() {
        String lookUp = lookUpScore(username);
        assertNotNull(lookUp);
    }

    public String lookUpScore(String us) {
        String sql4 = "select score from register where username=?";
        try {
            PreparedStatement sta = connection.prepareStatement(sql4);
            String username = us;
            sta.setString(1, username);
            ResultSet rs = sta.executeQuery();
            while (rs.next()) {
                System.out.println("The score for user " + us + " is " + Integer.parseInt(rs.getString("score")));
                return rs.getString("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}