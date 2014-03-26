package helpers;

import java.sql.*;

/**
 * 
 * @author stalin
 */
public class MysqlConnect {
    
	// static connection object ensures only one connection at a time
    static Connection con;
	
    public static Connection getConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            String url="jdbc:mysql://localhost/spam_filter?user=temp&password=temppass";
            con= DriverManager.getConnection(url);
            
            // return connection object
            return con;
        }
        catch(Exception se){
            se.printStackTrace();
            // return null if connection cannot be established
            return null;
        }
    }
	
    public static void closeConnection(Connection con, PreparedStatement ps){
    	// close connection and prepared statement object if not closed
        try{
        	if(ps!=null){
                ps.close();
            }
        	
            if(con!=null){
                con.close();
            }
        }
        catch(SQLException se){
            se.printStackTrace();
        }		
    }
}
