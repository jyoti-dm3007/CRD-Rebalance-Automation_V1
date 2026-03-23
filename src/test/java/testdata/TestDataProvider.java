package testdata;

import java.sql.*;
import java.util.*;

import db.DBConnection;
import models.RebalanceResult;
import models.Security;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TestDataProvider {
	
		public static Double totalAssetPrice() {
		return 100000d ;
	}

    public static List<Security> testData() {

		/*
		 * return Arrays.asList( new Security("IBM",20,10,150), new
		 * Security("MSFT",20,20,90), new Security("ORCL",20,30,220), new
		 * Security("AAPL",20,20,450), new Security("HD",20,20,70) );
		 */
        
       return getSecuritiesFromDB(1) ;   // Retrieve get data from DB table portfolio_securities
 
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
    
   
    
    public static BigDecimal calculateUpdatedPortfolioValue(
            List<Security> portfolio,
            Map<String, RebalanceResult> rebalanceMap,
            BigDecimal totalPortfolioValue) {

        BigDecimal newTotalValue = BigDecimal.ZERO;

        for (Security sec : portfolio) {

            BigDecimal price = BigDecimal.valueOf(sec.unitPrice);

            // Step 1: current value = total * %   --- calculation of current_value
            BigDecimal currentValue = totalPortfolioValue
                    .multiply(BigDecimal.valueOf(sec.currentPerAsset))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.DOWN);

            // Step 2: Count current shares base on current_prcent
            BigDecimal currentShares = currentValue
                    .divide(price, 10, RoundingMode.DOWN)
                    .setScale(2, RoundingMode.DOWN);

            // Step 3: rebalance
            RebalanceResult result = rebalanceMap.get(sec.name);

            BigDecimal updatedShares = currentShares;

            if (result != null) {
                BigDecimal rebalanceShares = BigDecimal.valueOf(result.getShares())
                        .setScale(2, RoundingMode.DOWN);

                switch (result.getAction().toUpperCase()) {
                    case "BUY":
                        updatedShares = updatedShares.add(rebalanceShares);
                        break;
                    case "SELL":
                        updatedShares = updatedShares.subtract(rebalanceShares);
                        break;
                    case "NONE":
                        break;
                }
            }

            // Ensure 2 decimal places
            updatedShares = updatedShares.setScale(2, RoundingMode.DOWN);

            // Step 4: updated value
            BigDecimal updatedValue = updatedShares.multiply(price)
                    .setScale(2, RoundingMode.DOWN);

            System.out.println(
                    sec.name +
                            " | Action: " + (result != null ? result.getAction() : "NONE") +
                            " | Shares: " + updatedShares +
                            " | Value: " + updatedValue
            );

            newTotalValue = newTotalValue.add(updatedValue);
        }

        return newTotalValue.setScale(2, RoundingMode.DOWN);
    }
    
    
    public static List<Security> getSecuritiesFromDB(int portfolio_id) {

            List<Security> list = new ArrayList<>();

          
            String query = "SELECT portfolio_id, sec_name, target_percent, current_percent, unit_price, created_at  "
            		+ " FROM public.portfolio_securities where portfolio_id ="+portfolio_id;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Security sec = new Security(
                            rs.getString("sec_name"),
                            rs.getDouble("target_percent"),
                            rs.getDouble("current_percent"),
                            rs.getDouble("unit_price")
                    );
                    list.add(sec);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
    }
    
    // remove these methods
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
	//	HashMap<String, RebalanceResult> expectedData = expectedShareCountMap();
		//iteratePrintHashMap(expectedData);
	//	listIteration(testData());
		
	//	System.out.print("Total value" + calculateUpdatedPortfolioValue(testData(),expectedShareCountMap(),BigDecimal.valueOf(100000d)));
		
		System.out.println(getSecuritiesFromDB(1));
    }
    	

	
		
	}
    
    
