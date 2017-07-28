import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;


public class MailParseCheck {

	/**
	 * @param args
	 * @throws Exception 
	 */
	static String[] topics = 
			new String[]{"regulations",
			"internal projects",
			"company image",
			"political influence",
			"california energy crisis",
			"internal company policy and operations",
			"alliances and partnership",
			"legal advice",
			"talking points",
			"meetings",
			"trip reports"};
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Map DB instance
		DB db = DBMaker
		            .newDirectMemoryDB().asyncFlushDelay(100)
		            .compressionEnable()
		            .make();
		Set<String> recoveredIndex = db.createTreeSet("test", 120, false, null, null);
		File emailFile=new File("D:/Fall 2015/COM S 561/Project/enron_mail_20150507/maildir/");
		TestClass testCla = new TestClass();
		testCla.writeJSon();
		StopWordRemover stopWordRemover = new StopWordRemover();
		List allSynList = testCla.readJsonMap();
		LinkedHashMap wordMailMap = testCla.prepareWordMailMap();
		LinkedHashMap synMap = new LinkedHashMap();
		for(int i = 0 ; i< allSynList.size(); i++)
		{
			synMap.put(allSynList.get(i).toString(), 0);
		}
		
		String afterStopWord[];
		try(
			      InputStream file = new FileInputStream("D:/mailMap.ser");
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
		long startTime = System.nanoTime();
		int mailListCount = 0;
		//getWholeGraph(recoveredIndex);
		
		for(String actualMailPath : recoveredIndex )
		{
			Session mailSession = Session.getDefaultInstance(new Properties());
			
			try
			{
				InputStream source = new FileInputStream(actualMailPath);
				MimeMessage message = new MimeMessage(mailSession, source);
				String msgContent = message.getContent().toString();
				String tokenizedMail [] = msgContent.split(" ");
				afterStopWord = stopWordRemover.removeStopWord(tokenizedMail);
			
					for(int ind = 0;ind < afterStopWord.length; ind ++)
					{
						if(synMap.containsKey(afterStopWord[ind]))
						{
							LinkedHashMap synMailMap = (LinkedHashMap)wordMailMap.get(afterStopWord[ind]);
							int frequency = (Integer)synMap.get(afterStopWord[ind]) + 1;
							synMailMap.put("frequency",frequency);
							List mailList = (List)synMailMap.get("mailList");
							mailList.add(actualMailPath);
							synMailMap.put("mailList", mailList);
							synMap.put(afterStopWord[ind], frequency);
							mailListCount += mailList.size();
						}
					}
				
				source.close();
				
			}		
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
		 
		
		try (
			      OutputStream file = new FileOutputStream("D:/synMap.ser");
			      OutputStream buffer = new BufferedOutputStream(file);
			      ObjectOutput output = new ObjectOutputStream(buffer);
			    ){
			      output.writeObject(wordMailMap);
			    }  
			    catch(IOException ex){
			     
			    }
		
		
		int topFiveMailCount = 0;
		LinkedHashMap synMaps = new LinkedHashMap();
		/*List topFiveSyn = new ArrayList();
		 * 
		try(
			      InputStream file = new FileInputStream("D:/synMap.ser");
			      InputStream buffer = new BufferedInputStream(file);
			      ObjectInput input = new ObjectInputStream (buffer);
			    )
			{
			      //deserialize the List
				synMaps = (LinkedHashMap)input.readObject();
			    
			}
		*/
		
		List topFiveSyn = testCla.topFiveSynMap(wordMailMap);
		for(int i =0; i< topFiveSyn.size(); i++)
		{
			LinkedHashMap topFiveMap = (LinkedHashMap)topFiveSyn.get(i);
			List mailList = (ArrayList)topFiveMap.get("mailList");
			topFiveMailCount += mailList.size();
		}
		int count = 0;
		int mailNo = 0;
		LinkedHashMap finalMessageMap = new LinkedHashMap();
		Session mailSession = Session.getDefaultInstance(new Properties());
		for(int i =0; i< topFiveSyn.size(); i++)
		{
			LinkedHashMap topFiveMap = (LinkedHashMap)topFiveSyn.get(i);
			List mailList = (ArrayList)topFiveMap.get("mailList");
			for(int mail = 0 ; mail < mailList.size();mail ++)
			{
				InputStream source = new FileInputStream(mailList.get(mail).toString());
				MimeMessage message = new MimeMessage(mailSession, source);
				String fromAddress = message.getFrom()[0].toString();
				String mailFrom = topFiveMap.get("topic").toString() + '_'+message.getMessageID() + '_' + fromAddress;
				if(!finalMessageMap.isEmpty())
				{
					if(!finalMessageMap.containsKey(mailFrom))
					{
						mailNo++;
						List toList = getRecipientListforMessage(message);
						if(!toList.isEmpty())
						{
							finalMessageMap.put(mailFrom,toList);
						}
					}
				}
				else
				{
					List toList = getRecipientListforMessage(message);
					if(!toList.isEmpty())
					{
						finalMessageMap.put(mailFrom,toList);
					}
				}
			}
			
		}
		
		writeCSVs(finalMessageMap);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		
	
	}
	private static void getWholeGraph(Set<String> mailset) throws Exception
	{
		LinkedHashMap edgeMap = new LinkedHashMap();
		int mailCount = 0;
		int idCount = 0;
		LinkedHashMap idUsers = new LinkedHashMap();
		for(String actualMailPath : mailset )
		{
			mailCount++;
			Session mailSession = Session.getDefaultInstance(new Properties());
			
			try
			{
				InputStream source = new FileInputStream(actualMailPath);
				MimeMessage message = new MimeMessage(mailSession, source);
				String fromAddress = message.getFrom()[0].toString();
				List toList = getRecipientListforMessage(message);
				if(!toList.isEmpty())
				{
					for(int ind = 0; ind < toList.size(); ind ++)
					{
						String toAddress = toList.get(ind).toString();
						if(!fromAddress.equals(toAddress))
						{
							int compareIndex = fromAddress.compareTo(toAddress);
							if(compareIndex > 0)
							{
								String temp = fromAddress;
								fromAddress = toAddress;
								toAddress = temp;
							}
							String addressPair = fromAddress + ":" + toAddress;
							if(!edgeMap.isEmpty())
							{
								if(edgeMap.containsKey(addressPair))
								{
									int weight = (int)edgeMap.get(addressPair);
									edgeMap.put(addressPair, (weight+1));
								}
								else
								{
									edgeMap.put(addressPair, 1);
									if(!idUsers.isEmpty())
									{
										if(!idUsers.containsKey(fromAddress))
										{
											idCount ++;
											idUsers.put(fromAddress,idCount);
										}
										if(!idUsers.containsKey(toAddress))
										{
											idCount ++;
											idUsers.put(toAddress,idCount);
										}

									}
									else
									{
										idCount ++;
										idUsers.put(fromAddress,idCount);
										idCount ++;
										idUsers.put(toAddress,idCount);
									}
								}
							}
							else
							{
								idCount++;
								idUsers.put(fromAddress, idCount);
								edgeMap.put(addressPair, 1);
								idCount++;
								idUsers.put(toAddress, idCount);
							}
						}
						
						
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		writeToCsv(edgeMap,"WholeGraph",idUsers);
	}
	/*
	 * Write to topic CSVs
	 */
	private static void writeCSVs(LinkedHashMap finalMessageMap) throws Exception
	{
		 Set<String> keys = finalMessageMap.keySet();
		 LinkedHashMap idUsers = new LinkedHashMap();
		 LinkedHashMap csvMap = new LinkedHashMap();
		 int wordCount = -1;
		 String topicSearch = "@@@@@";
		 int count = 0;
		 for(String k:keys)
		 {
			 if(!k.contains(topicSearch))
			 {
				 if(wordCount != -1)
				 {
					 writeToCsv(csvMap,topicSearch,idUsers);
				 }
				 if(wordCount == topics.length -1)
				 {
					break;
				 
				 }
				 else
				 {
					 wordCount ++;
					 topicSearch = topics[wordCount];
					 csvMap = new LinkedHashMap();
				 }
			 }
			 String keySplit[] = k.split("_");
			String fromAddress = keySplit[2];
			List toList = (List) finalMessageMap.get(k);
			for(int ind = 0; ind < toList.size(); ind ++)
			{
				String toAddress = toList.get(ind).toString();
				if(!fromAddress.equals(toAddress))
				{
					int compareIndex = fromAddress.compareTo(toAddress);
					if(compareIndex > 0)
					{
						String temp = fromAddress;
						fromAddress = toAddress;
						toAddress = temp;
					}
					String addressPair = fromAddress + ":" + toAddress;
					if(!csvMap.isEmpty())
					{
						if(csvMap.containsKey(addressPair))
						{
							int weight = (int)csvMap.get(addressPair);
							csvMap.put(addressPair, (weight+1));
						}
						else
						{
							csvMap.put(addressPair, 1);
							if(!idUsers.isEmpty())
							{
								if(!idUsers.containsKey(fromAddress))
								{
									count ++;
									idUsers.put(fromAddress,count);
								}
								if(!idUsers.containsKey(toAddress))
								{
									count ++;
									idUsers.put(toAddress,count);
								}

							}
							else
							{
								count ++;
								idUsers.put(fromAddress,count);
								count ++;
								idUsers.put(toAddress,count);
							}
						}
					}
					else
					{
						count ++;
						idUsers.put(fromAddress,count);
						count ++;
						idUsers.put(toAddress,count);
						csvMap.put(addressPair, 1);
					}
				}
				
				
			}
			 
		 }
	}
	/*
	 * Calls a helper class to write CSV files
	 * @params myHashMap ,topic , addressMap
	 */
	private static void writeToCsv(LinkedHashMap myHashMap, String topic, LinkedHashMap addressMap) throws Exception
	{
		List writeData = new ArrayList();
		for (Object key : myHashMap.keySet())
    	{
    			String node[]= key.toString().split(":");
    			String nodes = addressMap.get(node[0])+ "," + addressMap.get(node[1]);
    			
    			String line= nodes + "," + myHashMap.get(key).toString();
    			
    			writeData.add(line);
    	}
		tetsingSeri.writeToCsv(writeData, topic);
	}

	/*
	 * Get Recipient List for Messages
	 * @return TO List Of Messages
	 */
	
	private static List getRecipientListforMessage(MimeMessage message) throws MessagingException 
	{
		// TODO Auto-generated method stub
		
		Set recipientSet = new HashSet();
		int entireListLength = 0;
		if(message.getRecipients(Message.RecipientType.TO) != null)
		{
			entireListLength += message.getRecipients(Message.RecipientType.TO).length; 
			for(int ind = 0 ; ind<message.getRecipients(Message.RecipientType.TO).length;ind++)
			{
				String toMail = message.getRecipients(Message.RecipientType.TO)[ind].toString();
				if(toMail.contains("@enron.com"))
					recipientSet.add(message.getRecipients(Message.RecipientType.TO)[ind].toString());
			}
		}
		if(message.getRecipients(Message.RecipientType.BCC) != null)
		{
			entireListLength += message.getRecipients(Message.RecipientType.BCC).length; 
			for(int ind = 0 ; ind<message.getRecipients(Message.RecipientType.BCC).length;ind++)
			{
				String bccMail = message.getRecipients(Message.RecipientType.BCC)[ind].toString();
				if(bccMail.contains("@enron.com"))
					recipientSet.add(message.getRecipients(Message.RecipientType.BCC)[ind].toString());
			}
		}
		if(message.getRecipients(Message.RecipientType.CC) != null)
		{
			entireListLength += message.getRecipients(Message.RecipientType.CC).length; 
			for(int ind = 0 ; ind<message.getRecipients(Message.RecipientType.CC).length;ind++)
			{
			String ccMail = message.getRecipients(Message.RecipientType.CC)[ind].toString();
			if(ccMail.contains("@enron.com"))
				recipientSet.add(message.getRecipients(Message.RecipientType.CC)[ind].toString());
			}
		}
		Object[] objectArray = recipientSet.toArray();
		String[] recipientList = (String[])recipientSet.toArray(new String[recipientSet.size()]);
		List recList = Arrays.asList(recipientList);
		return recList;
		
	}

}
