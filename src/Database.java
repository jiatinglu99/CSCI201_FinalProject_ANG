import java.sql.*;

/**
 * @author Yunhan Mao at 2020/11/20
 * @using IntelliJ IDEA
 */
public class Database {
    private static Connection connection;
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        /**
         * 1.注册（先查， 如果已存在 提示并重新要求注册 再增）
         * 2.登录验证（只查， 密码错误则回到验证,如果不存在用户名则重定向到注册页面）
         * 3.分数改动（猜对了加一分， 并且还要可以查询特定用户名的分数） 已经在数据库设置为唯一索引
         */
        //url
        String jdbcUrl = "jdbc:mysql://localhost:3306/fpdatabase";
        //user
        String db_user = "root";
        //password
        String db_password = "root";//"dd001127";
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(jdbcUrl, db_user, db_password);
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

    public Boolean addScore(String us){
        String sql3="update register SET score=score+1 where username=?";
        try{
            PreparedStatement sta = connection.prepareStatement(sql3);
            sta.setString(1, us);
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
}
