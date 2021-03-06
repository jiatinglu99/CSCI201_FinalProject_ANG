import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author Yunhan Mao at 2020/11/20
 * @using IntelliJ IDEA
 */
public class Database {
    private static Connection connection;

    public Database() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("jdbc.properties"));
            Class.forName(properties.getProperty("classURL"));
            connection = DriverManager.getConnection((String) properties.get("jdbcURL"), properties.getProperty("username"), properties.getProperty("password"));
        } catch (Exception c){
            c.printStackTrace();
        }
    }

    public Boolean register(String us, String pd){
        String sql1 = "insert into register(username,password,score) values(?,?,?);";
        PreparedStatement preStmt=null;
        String username=us;
        String pwd=pd;
        int score=0;
        try{
            preStmt= connection.prepareStatement(sql1);
            preStmt.setString(1, username);
            preStmt.setString(2, pwd);
            preStmt.setInt(3, score);
            int i = -1;
            i = preStmt.executeUpdate();
            if(i>=1){
                return true;
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean login(String us, String pd){
        String sql2 = "select * from register where username=? and password=?;";
        try {
            PreparedStatement sta = connection.prepareStatement(sql2);
            String username=us;
            String pwd=pd;
            sta.setString(1, username);
            sta.setString(2, pwd);
            ResultSet rs = sta.executeQuery();
            if (rs.next()) {
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
   public Boolean addScore(String us, int score){
        String sql3="update register SET score=score+? where username=?";
        int scoreToAdd=score;
        try{
            PreparedStatement sta = connection.prepareStatement(sql3);
            sta.setInt(1, scoreToAdd);
            sta.setString(2, us);
            int j=-1;
            j=sta.executeUpdate();
            if(j>=1){
                return true;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String lookUpScore(String us){
        String sql4="select score from register where username=?";
        try{
            PreparedStatement sta = connection.prepareStatement(sql4);
            String username=us;
            sta.setString(1, username);
            ResultSet rs = sta.executeQuery();
            while (rs.next()) {
                System.out.println("The score for user "+us+" is "+ rs.getString("score"));
                return rs.getString("score");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}