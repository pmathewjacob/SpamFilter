package main;

import helpers.MysqlConnect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author stalin
 * 
 * This is a temporary class, used for secondary purposes
 */
public class Test {
	
	private static Connection con;
	private static Statement s;
	private static ResultSet rs;
	
	private static File outputFile;
	private static BufferedWriter bw;
	
	private final static String SQL_QUERY = "select * from spam_words order by frequency desc";
	
	public Test(){
		con = null;
		s = null;
		rs = null;
		
		outputFile = new File("/home/kunal/My Disk/Minor Project/Output/Output.txt");
		try {
			bw = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args){
    	new Test();
    	
        con = MysqlConnect.getConnection();
        
        try {
			s = con.createStatement();
			rs = s.executeQuery(SQL_QUERY);
			
			try {
				bw.write("WORD \t\t\t\t FREQUENCY\n");
				bw.write("-----------------------\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(rs.next()){
				String word = rs.getString(1);
				String frequency = Integer.toString(rs.getInt(2));
				
				try {
					bw.write(word + "\t\t\t\t" + frequency + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			con = null;
			s = null;
		}
    }
}
