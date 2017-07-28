import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Set;
import org.mapdb.*;

/*
 * Class to get all the mail files and serialize them onto a MapDB object
 */
public class CheckFolders {

	/**
	 * @param File
	 * @return Set of mail files after recursive search in the directory
	 */
	public static Collection<String> listFileTree(File dir) {
	    Set <String> fileTree = new TreeSet<String>();
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile()) 
	        {
	        	fileTree.add(entry.toString());
	        }
	        else
	        {
	        	if(!entry.toString().contains(("_sent_mail")))
	        	{
	        		fileTree.addAll(listFileTree(entry));
	        	}
	        }
	    }
	    return fileTree;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File emailFile=new File("D:/Fall 2015/COM S 561/Project/enron_mail_20150507/maildir/");
		DB db = DBMaker
	            .newDirectMemoryDB().asyncFlushDelay(100)
	            .compressionEnable()
	            .make();
	   Set listFiles = db.createTreeSet("test", 120, false, null, null);
	   listFiles = (TreeSet) listFileTree(emailFile);	
		try (
			      OutputStream file = new FileOutputStream("D:/addressIn.ser");
			      OutputStream buffer = new BufferedOutputStream(file);
			      ObjectOutput output = new ObjectOutputStream(buffer);
			    ){
			      output.writeObject(listFiles);
			    }  
			    catch(IOException ex){
			     
			    }
		db.commit();
		db.close();
		
	}
}
