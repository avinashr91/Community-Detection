import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
/*
 * Helper class for all sub methods
 */
public class TestClass
{
	JSONParser parser = new JSONParser();
	public List allWordSynList = new ArrayList();
	public LinkedHashMap wordMailMap= new LinkedHashMap();
	public List wordMatchSyn = new ArrayList();
	String wordForm[] = new String [] {"regulations",
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
	/*
	 * writes the synonyms on to a JSON file
	 * @throws Exception
	 */
	public void writeJSon() throws Exception
    {
		long startTime = System.nanoTime();
	   JSONArray words = new JSONArray();
	   JSONObject wordsJson = new JSONObject();
	   StopWordRemover stopRemover = new StopWordRemover();
	   File f=new File("C:/Program Files (x86)/WordNet/2.1_Copy/dict");
	   System.setProperty("wordnet.database.dir", f.toString());
	   //setting path for the WordNet Directory
	   for(int index = 0; index < wordForm.length; index++)
	   {
		   JSONObject synonym = new JSONObject();
		   synonym.put("word", wordForm[index]);
		   JSONArray listSynonym = new JSONArray();
	       String []splitWords = wordForm[index].split(" ");
	       String []afterStop = stopRemover.removeStopWord(splitWords);
	       ArrayList<String> al = new ArrayList<String>();
	       WordNetDatabase database = WordNetDatabase.getFileInstance();
	       for(int ind = 0; ind < afterStop.length; ind++)
	       {
	    	   Synset[] synsets = database.getSynsets(afterStop[ind],SynsetType.NOUN);
	    	   //Display the word forms and definitions for synsets retrieved
	    	   if (synsets.length > 0)
	    	   {
	              // add elements to al, including duplicates
	              HashSet hs = new HashSet();
	              for (int i = 0; i < synsets.length; i++)
	              {
	                 String[] wordForms = synsets[i].getWordForms();
	                 for (int j = 0; j < wordForms.length; j++)
	                 {
	                	 al.add(wordForms[j]);
	                 }
	                 //removing duplicates
	                 hs.addAll(al);
	                 al.clear();
	                 al.addAll(hs);
	                 //showing all synsets
	                 
	              }
	           }
	    	   else
	    	   {
	    		   al.add(afterStop[ind]);
	    	   }
	       }
	       for(int len = 0; len < al.size(); len ++)
	       {
	    	   listSynonym.add(al.get(len));
	       }
	       synonym.put("synonyms", listSynonym);
	       words.add(synonym);
	   }
	   wordsJson.put("Detail_synonym", words);
	   
	   try {

			FileWriter file = new FileWriter("D:/synonymDict.json");
			file.write(wordsJson.toString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	  
   } 
	/*
	 * Reads the synonymns from the JSON 
	 * @return list of synonyms
	 */
	public List readJsonMap() throws Exception
	{
		String [] synWords;
		HashSet synSet = new HashSet();
		Object obj = parser.parse(new FileReader("D:/synonymDict.json"));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray jsonArray = (JSONArray)jsonObject.get("Detail_synonym");
		for(int index = 0; index < jsonArray.size(); index++)
		{
			JSONObject wordMap = (JSONObject)jsonArray.get(index);
			
			String word = wordMap.get("word").toString();
			List synList = (List)wordMap.get("synonyms");
			for(int i = 0; i< synList.size(); i++)
			{
				allWordSynList.add(synList.get(i).toString());
			}
			
			LinkedHashMap wordMatchSynonym = new LinkedHashMap();
			wordMatchSynonym.put(word, synList.size());
			wordMatchSyn.add(wordMatchSynonym);
		}
		return allWordSynList;
	}
	/*
	 * Prepares the basic synonym map with frequency and mail list
	 * @return the basic synonym Map
	 */
	public LinkedHashMap prepareWordMailMap() throws Exception
	{
		
		int count = -1;
		
		int no_of_synos = 0;
		
		for(int index = 0 ; index < allWordSynList.size(); index++)
		{
			if(index == no_of_synos)
			{
				count ++;
				LinkedHashMap wordMatch = (LinkedHashMap)wordMatchSyn.get(count);
				no_of_synos += (int) wordMatch.get(wordForm[count]);
			}
			LinkedHashMap synMap = new LinkedHashMap();
			synMap.put("word",allWordSynList.get(index).toString());
			synMap.put("topic",wordForm[count]);
			synMap.put("frequency",0);
			synMap.put("mailList",new ArrayList());
			wordMailMap.put(allWordSynList.get(index).toString(),synMap);
			
		}
		//System.out.println(wordMailMap);
		return wordMailMap;
	}
	/*
	 * params the Synonym map with the frequency of synonyms and the maillist
	 * return Top five synonyms for evvery topic
	 */
	public List topFiveSynMap(LinkedHashMap<String,Map> synMailMap) throws Exception
	{
		List resultFiveSyn = new ArrayList();
		List subSynList = null;
		int no_ofSyns = 0;
		int count = -1;
		int index = 0;
		
		for(String key : synMailMap.keySet())
		{
			if(index == no_ofSyns)
			{
				if(index != 0)
				{
					Collections.sort(subSynList,new MyMapComparator());
					for(int inner = 0; inner < subSynList.size(); inner ++)
					{
						if(inner <5)
						{
							LinkedHashMap synoMap = (LinkedHashMap)subSynList.get(inner);
							if((int)synoMap.get("frequency") != 0)
							{
								resultFiveSyn.add(synoMap);
							}
						}
						else
						{
							break;
						}
					}
				}
				subSynList = new ArrayList();
				count ++;
				LinkedHashMap wordMatch = (LinkedHashMap)wordMatchSyn.get(count);
				no_ofSyns += (int) wordMatch.get(wordForm[count]);
			}
			LinkedHashMap synMap = (LinkedHashMap)synMailMap.get(key);
			subSynList.add(synMap);
			index ++;
		}
		
		
		return resultFiveSyn;
	}
	
}