import java.util.Comparator;
import java.util.Map;
/*
 * Class to sort the hashmaps based on the frequency of synonymns
 */
public  class MyMapComparator implements Comparator 
{
   
	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		Map m1 =(Map)o1;
		Map m2 = (Map)o2;
		 return m2.get("frequency").toString().compareTo(m1.get("frequency").toString());
	}
}