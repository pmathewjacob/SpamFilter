package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helpers.FileReader;
import helpers.HTMLParser;
import helpers.MysqlConnect;

/**
 *
 * @author stalin
 */
public class MainClass { 
	// Set to store a collection of stop words
	private static Set<String> stopWords;
    // HashMap to map words to their frequencies
    private static Map<String, Integer> keywordsMap;
    // HashMap to hold integer to character mapping
    private static Map<Integer,Character> digitToCharacterMap;
    
    // file containing stop words
    File stopWordsFile = new File("src/files/stop_words.txt");
    
    // connection and prepared statement objects for database
    private static Connection con;
    private static PreparedStatement ps;
    
    // MySQL insert query
    private final static String SQL_INSERT = "insert into spam_words values(?,?)";
    
    public MainClass(){
    	// instantiate keywords map
    	keywordsMap = new HashMap<String, Integer>();
    	
        // Set containing all stop words
    	stopWords = new HashSet<String>();
    	
    	// read all stop words from file and add it to our stopWords Set
    	BufferedReader br = null;
    	
    	try {
			br = new BufferedReader(new java.io.FileReader(stopWordsFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("stop_words.txt file not found!");
			e.printStackTrace();
		}
    	
    	String stopWord;
    	try {
			while((stopWord = br.readLine())!=null){
				stopWords.add(stopWord);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot read from stop_words.txt file!");
			e.printStackTrace();
		}
    	
    	// initialize digitToCharacterMap
    	digitToCharacterMap = new HashMap<Integer,Character>();
    	digitToCharacterMap.put(0, 'o');
    	digitToCharacterMap.put(1, 'l');
    	digitToCharacterMap.put(2, 'z');
    	digitToCharacterMap.put(3, 'e');
    	digitToCharacterMap.put(5, 's');
    	digitToCharacterMap.put(6, 'g');
    	digitToCharacterMap.put(7, 't');
    	digitToCharacterMap.put(8, 'b');
    	digitToCharacterMap.put(9, 'g');
    	
    	// database connection and prepared statement objects
    	con = null;
    	ps = null;
    }
     
    public static void main(String[] args) throws IOException{   
    	// initialize MainClass
    	new MainClass();
    	
        // get the array of all files
        //File[] allFiles = FileReader.readAll("/home/kunal/My Disk/Minor Project/Spam Data/Spams/");
    	File[] allFiles = FileReader.readAll("/home/kunal/My Disk/Minor Project/Test Data 1/");
        HTMLParser parser;
        
        // process each file
        for(File file : allFiles){
            // initialize HTML parser
            parser = new HTMLParser(file);
            
            // parse the file, remove HTML
            String parsedFile = parser.parse();
            System.out.println();
            System.out.println(parsedFile);
            
            // add the words into HashMap
            getWordMap(parsedFile.toLowerCase());
            
            // UNIT TEST
            //System.out.println(keywordsMap);
        }
        
        // add all words to database
        fillDB();
    }
    
    // subroutine to create HashMap containing words and their frequencies
    private static void getWordMap(String parsedFile){
    	/*
    	 * Pattern explanation: [0-9]*[a-z_'|]+[0-9]*
    	 * 	
    	 * 	1. Matches any alphanumeric string, having at least 1 alphabet
    	 *  2. _ (underscore) added to match tricky words such as "so_ft_wa_re"
    	 *  3. ' (apostrophe) added to match complete strings of type "command's"
    	 *  4. | (pipes) added to match tricky words such as "|tems", "benef|t"
    	 *  
    	 *  * Note: Keep adding special symbols in [a-z_'|] as you figure out new tricks and list them accordingly
    	 */
        Pattern tokenPattern = Pattern.compile("[0-9]*[a-z_'|]+[0-9]*");
        Matcher m = tokenPattern.matcher(parsedFile);
        
        while(m.find()){
        	// get token
            String token = m.group();
            
            // check if token is a stop word 
            if(stopWords.contains(token) || token.length() > 255 || token.length() < 3){
            	// ignore stop word
            	continue;
            }
            
            // else add the word to map accordingly
            if(keywordsMap.containsKey(token)){
                keywordsMap.put(token, keywordsMap.get(token)+1);
            } else {
            	keywordsMap.put(token, 1);
            }
        }
    }
    
    // routine which maps digits to alphabets, such as 0 (zero) -> o
    private static String replaceDigits(String token){
    	char alphabets[]=token.toCharArray();
    	
    	// replace each digit by it's expected alphabet
    	for(int i=0;i<alphabets.length;i++){
    		if(Character.isDigit(alphabets[i]))
    			alphabets[i]=digitToCharacterMap.get(alphabets[i]);
    	}
    	
    	// string containing only alphabets
    	String str=alphabets.toString();
    	return str;
    }
    
    // routine to store words in databse
    private static void fillDB(){
    	// initialize the connection and prepared statement objects
    	con = MysqlConnect.getConnection();
    	try {
			ps = con.prepareStatement(SQL_INSERT);
			
			// iterate through each entry in keywordsMap and add it to batch
			// execute batch after every 1000 entries
			int i = 0;
			for(Iterator<String> it = keywordsMap.keySet().iterator(); it.hasNext() ; ){
				i++; 
				
				String word = it.next();
				int frequency = keywordsMap.get(word);
				
				ps.setString(1, word);
				ps.setInt(2, frequency);
				
				// add entries in the batch
				ps.addBatch();
				
				// check if 1000 entries added
				if(i%1000 == 0){
					// execute the batch of 1000 entries
					ps.executeBatch();
					
					// start count for 1000 again
					i = 0;
				}
			}
			
			// execute batch again, some entries < 1000 may be present
			ps.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			// close database connection and delete prepared statement object
			MysqlConnect.closeConnection(con, ps);
		}
    }
}
