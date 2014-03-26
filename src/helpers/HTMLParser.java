package helpers;

import java.io.File;
import java.io.IOException;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

/**
 *
 * @author stalin
 */
public class HTMLParser {
    // location of file containing HTML text
    Source source;
    // string containing parsed HTML text from source
    String parsedDoc;
    
    public HTMLParser(File file) throws IOException{
        source = new Source(file); 
    }
    
    public String parse(){    
        TextExtractor te = new TextExtractor(source);
        // following line can be used to include/ignore certain tag attributes
        //te.setIncludeAttributes(true);
        
        return te.toString();
    }
}
