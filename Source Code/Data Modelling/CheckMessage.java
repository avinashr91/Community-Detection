import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.mapdb.DB;
import org.mapdb.DBMaker;
/*
 * Class to delete the duplicate and empty mails and also to delete non enron.com emails. Serializes the resulting mails onto MapDB object
 */
public class CheckMessage {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Session mailSession = Session.getDefaultInstance(new Properties());
		HashMap mailMap = new LinkedHashMap();
		DB db = DBMaker
	            .newDirectMemoryDB().asyncFlushDelay(100)
	            .compressionEnable()
	            .make();
		Set finalMapSet = db.createTreeSet("test", 120, false, null, null);
		finalMapSet = new TreeSet();
		
		Set<String> recoveredIndex = new TreeSet<String>();
		try(
			      InputStream file = new FileInputStream("D:/addressIn.ser");
			      InputStream buffer = new BufferedInputStream(file);
			      ObjectInput input = new ObjectInputStream (buffer);
			    )
			{
			      //deserialize the List
			      recoveredIndex = (TreeSet)input.readObject();
			     
			}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int count = 0;
		
	    int fileCount = 0;
	    int totalCount = 0;
		for(String actualMailPath : recoveredIndex )
		{
			try
			{
				InputStream source = new FileInputStream(actualMailPath);
				MimeMessage message = new MimeMessage(mailSession, source);
				int msgSize = message.getSize();
				
				String msgDate= message.getSentDate().toString(); 
				String msgFrom = message.getFrom()[0].toString();
				if(msgFrom.contains("@enron.com") == true && message.getContent().toString().isEmpty() == false)
				{
					String msgID = msgDate + msgFrom;
					if(!mailMap.isEmpty())
					{
						if(!mailMap.containsKey(msgID))
						{
							mailMap.put(msgID, actualMailPath);
							finalMapSet.add(actualMailPath);
						}
						
					}
					else
					{
						mailMap.put(msgID, actualMailPath);
						finalMapSet.add(actualMailPath);
					}
				}
			}
			catch(Exception e)
			{
				
				e.printStackTrace();
				
			}
		}
		
		try (
			      OutputStream file = new FileOutputStream("D:/mailMap.ser");
			      OutputStream buffer = new BufferedOutputStream(file);
			      ObjectOutput output = new ObjectOutputStream(buffer);
			    ){
			      output.writeObject(finalMapSet);
			    }  
			    catch(IOException ex){
			     
			    }
		db.commit();
		db.close();
	
	}	

}
