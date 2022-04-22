import java.sql.*;
import java.util.Properties;

public class Converter {

    public static void main(String[] args) throws Exception {
        try(Connection oldDatabaseConnection = getJPREMDatabaseConnection();
            Connection newDatabaseConnection = getDBADatabaseConnection();

            Statement statement = oldDatabaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM `" + "user_profiles" + "`")) {
            while (resultSet.next()) {
                PreparedStatement preparedStatement = newDatabaseConnection.prepareStatement("INSERT INTO `playerdata` (`uuid`, `name`, `email`, `reg_ip`, `log_ip`, `password`, `salt`, `firstjoin`, `lastjoin`, `premium`, `valid`, `server`, `lwlogged`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                String nickname = resultSet.getString("lastNickname");
                int premInt = 0;
                try {
                    if (resultSet.getString("premiumId") == null) {
                        String uuid = getUniqueIdWithDashes(resultSet.getString("uniqueId"));
                        preparedStatement.setString(1, uuid);
                        premInt = 0;
                    }else {
                        String uuid = getUniqueIdWithDashes(resultSet.getString("premiumId"));
                        preparedStatement.setString(1, uuid);
                        premInt = 1;
                    }
                    preparedStatement.setString(2, nickname);
                    preparedStatement.setString(3, "null");
                    String IP = resultSet.getString("lastAddress");
                    if (IP == null){
                        IP = "null";
                    }
                    preparedStatement.setString(4, IP);
                    preparedStatement.setString(5, IP);
                    String password = resultSet.getString("hashedPassword");
                    preparedStatement.setString(6, getPassword(password, "pwd"));
                    preparedStatement.setString(7, getPassword(password, "salt"));
                    Timestamp firstJoin = resultSet.getTimestamp("firstSeen");
                    preparedStatement.setTimestamp(8, firstJoin);
                    Timestamp lastJoin = resultSet.getTimestamp("lastSeen");
                    preparedStatement.setTimestamp(9, lastJoin);
                    preparedStatement.setInt(10, premInt);
                    preparedStatement.setInt(11, 0);
                    preparedStatement.setString(12, null);
                    preparedStatement.setInt(13, 1);
                    preparedStatement.execute();
                    System.out.println("Add the player : " + nickname + "to DBA database");
                    wait(3);
                } catch (Throwable e) {
                    System.out.println("Could not to convert " + nickname + "!");
                    e.printStackTrace();
                }
            }
        }
        System.out.println("All data has been migrated");
    }


    // here add all SQL information of DynamicBungeeAuth database
    private static Connection getDBADatabaseConnection() throws SQLException {
        String host = "0.0.0.0";
        String database = "database";
        String user = "user";
        String password = "password";
        int port = 3306;
        boolean useSSL = false;
        String driverURL = "jdbc:mysql://" + host + ":" + port + "/" + database;
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", String.valueOf(useSSL));
        return DriverManager.getConnection(driverURL, properties);
    }

    // here add all SQL information of JPremium database
    private static Connection getJPREMDatabaseConnection() throws SQLException {
        String host = "0.0.0.0";
        String database = "database";
        String user = "user";
        String password = "password";
        int port = 3306;
        boolean useSSL = false;
        String driverURL = "jdbc:mysql://" + host + ":" + port + "/" + database;
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", String.valueOf(useSSL));
        return DriverManager.getConnection(driverURL, properties);
    }

    public static String getUniqueIdWithDashes(String uniqueId) {
        if(uniqueId == null){
            return null;
        }
        String p1 = ""; // 8 char
        String p2 = ""; // 4 char
        String p3 = ""; // 4 char
        String p4 = ""; // 4 char
        String p5 = ""; // le reste
        p1 = uniqueId.substring(0, 8);
        p2 = uniqueId.substring(8, 12);
        p3 = uniqueId.substring(12, 16);
        p4 = uniqueId.substring(16, 20);
        p5 = uniqueId.substring(20, uniqueId.length());

        String res = p1 + "-" + p2 + "-" + p3 + "-" + p4 + "-" + p5;
        return res;
    }

    public static String getPassword(String pwd, String type){
        if(pwd == null){
            return null;
        }
        String[] passwordsplit = pwd.split("\\$");
        if (type.equals("salt")){
            return passwordsplit[1];
        }else if (type.equals("pwd")){
            return passwordsplit[2];
        }else{
            return null;
        }
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

}
