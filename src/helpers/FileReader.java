package helpers;

import java.io.File;

/**
 *
 * @author stalin
 */
public class FileReader {
    
    // method to fetch array of all files
    public static File[] readAll(String dirLocation){
        // get the location of directory
        File filesLocation = new File(dirLocation); 
        // get array of all files inside directory
        File[] allFiles = filesLocation.listFiles();
        
        // return array of files
        return allFiles;
    }
}
