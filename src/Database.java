import java.sql.*;

/**
 * @author Yunhan Mao at 2020/11/20
 * @using IntelliJ IDEA
 */
public class Database {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        /**
         * 1.注册（先查， 如果已存在 提示并重新要求注册 再增）
         * 2.登录验证（只查， 密码错误则回到验证,如果不存在用户名则重定向到注册页面）
         * 3.分数改动（猜对了加一分， 并且还要可以查询特定用户名的分数） 已经在数据库设置为唯一索引
         */
        //url
        String jdbcUrl = "jdbc:mysql://localhost:3308/fpdatabase";
        //user
        String user = "root";
        //password
        String password = "dd001127";
        Class.forName("com.mysql.cj.jdbc.Driver");
        //////////////////////////////////////注册////////////////////////////////////////////////////////////////////////////////////
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        String sql1 = "insert into register(username,password,score) values(?,?,?);";
        PreparedStatement preStmt=null;
        String username="xiaomaogary";
        String pwd="dd001127";
        int score=0;
        try{
            preStmt= connection.prepareStatement(sql1);
            preStmt.setString(1, username);
            preStmt.setString(2, pwd);
            preStmt.setInt(3, score);
            int i = -1;
            i = preStmt.executeUpdate();
            if(i>=1){
                System.out.println("注册成功！");
            }
            else{
                System.out.println("注册失败！");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        //////////////////////////////////////登录////////////////////////////////////////////////////////////////////////////////////
        String sql2 = "select * from register where username=? and password=?;";
        try {
            PreparedStatement sta = connection.prepareStatement(sql2);
            sta.setString(1, username);
            sta.setString(2, password);
            ResultSet rs = sta.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString(3) + "登入成功");
            } else {
                System.out.println("账号或者密码输入不正确");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        //////////////////////////////////////加分////////////////////////////////////////////////////////////////////////////////////
        String sql3="update register SET score=score+1 where username=?";
        try{
            PreparedStatement sta = connection.prepareStatement(sql3);
            sta.setString(1, username);
            int j=-1;
            j=sta.executeUpdate();
            if(j>=1){
                System.out.println("加分成功！");
            }
            else{
                System.out.println("加分失败！");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
