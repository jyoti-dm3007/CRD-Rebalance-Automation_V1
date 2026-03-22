package testdata;

import models.RebalanceResult;
import models.Security;
import java.util.*;

public class TestDataProvider {

    public static List<Security> testData() {

        return Arrays.asList(
                new Security("IBM",20,10,150),
                new Security("MSFT",20,20,90),
                new Security("ORCL",20,30,220),
                new Security("AAPL",20,20,450),
                new Security("HD",20,20,70)
        );
        
        
    }
    
    public static HashMap<String, RebalanceResult> expectedShareCountMap() {

        return  new HashMap (Map.of(
                "IBM",new RebalanceResult("IBM",66.66333333333333 ,"BUY"),
                "MSFT",new RebalanceResult("MSFT",0d ,"NONE") ,
                "ORCL",new RebalanceResult("ORCL",45.45454545454545 ,"SELL") ,
                "AAPL",new RebalanceResult("AAPL",0d ,"NONE") ,
                "HD",new RebalanceResult("HD", 0d,"NONE") )) ;
        
    }
    
    public static void iteratePrintHashMap( HashMap<String, RebalanceResult> expectedData) {
    	
    	System.out.println(expectedData.get("IBM").getShares());
		
		for (Map.Entry<String, RebalanceResult> entry : expectedData.entrySet()) {
		    String key = entry.getKey();
		    RebalanceResult value = entry.getValue();
		    System.out.println("Key: " + key + ", Value: " + value);
		}    	
    }
    
    public static void listIteration(List<Security> securities) {
		
    	List<Security> filteredList =   securities.stream().
								    	filter(s -> s.name.equalsIgnoreCase("IBM")
								    			|| s.name.equalsIgnoreCase("MSFT")).
								    	toList() ;
    	
    	System.out.println("Filtered List: " + filteredList);
    	
    	List<Double> names = securities.stream().map(s -> s.unitPrice).toList() ;
    	System.out.println("Names List: " + names);
    	
    	 Security ibm = securities.stream()
    	            .filter(s -> "IBM".equals(s.name))
    	            .findFirst()
    	            .orElse(null);
    	
    	System.out.println("IBM Security: " + ibm);
    	
    }
    
    public static void main(String[] args) {
		HashMap<String, RebalanceResult> expectedData = expectedShareCountMap();
		//iteratePrintHashMap(expectedData);
		listIteration(testData());
    }
    	

	
		
	}
    
    
