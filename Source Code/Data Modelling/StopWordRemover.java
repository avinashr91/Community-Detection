import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.lti.ws4j.util.WS4JConfiguration;
/*
 * Class to remove the stop words from a string array
 */
final public class StopWordRemover {
        
        private static final StopWordRemover instance = new StopWordRemover();
        private Set<String> stoplist;
        
        /**
         * Private constructor 
         */
        StopWordRemover()
        {
                try {
                        loadStopList();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
        
        /**
         * Singleton pattern
         * @return singleton object
         */
        public static StopWordRemover getInstance()
        {
                return StopWordRemover.instance;
        }
        
        private synchronized void loadStopList() throws IOException 
        {
                String stoplistFilename = WS4JConfiguration.getInstance().getStopList();
                stoplist = new LinkedHashSet<String>(256);
                InputStream stream = StopWordRemover.class.getResourceAsStream( "/"+stoplistFilename ); 
                InputStreamReader isr = new InputStreamReader( stream );
                BufferedReader br = new BufferedReader( isr );
                String line = null;
                while ( ( line = br.readLine() ) != null ) {
                        String stopword = line.trim();
                        if ( stopword.length() > 0 ) {
                                stoplist.add( stopword );
                        }
                }
                br.close();
                isr.close();
        }
        
        // separate stop word class if needed
        public List removeStopWords( String[] words ) {
                List<String> contents = new ArrayList<String>( words.length );
                for ( String word : words ) {
                        if ( ! stoplist.contains( word ) ) {
                                contents.add( word );
                        }
                }
                //String[] result = (String[]) contents.toArray(new String[contents.size()]);
                return contents;
        }       
        
        public String[] removeStopWord( String[] words ) {
            List<String> contents = new ArrayList<String>( words.length );
            for ( String word : words ) {
                    if ( ! stoplist.contains( word ) ) {
                            contents.add( word );
                    }
            }
            String[] result = (String[]) contents.toArray(new String[contents.size()]);
            return result;
    }       
}