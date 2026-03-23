package tests.service;

import base.BaseTest;
import models.Security;
import models.RebalanceResult;
import pages.PortfolioPage;
import services.RebalanceService;
import testdata.TestDataProvider;
import dao.QueryExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebalanceTest extends BaseTest {

    @Test
    public void validateRebalanceAPI() {

        double totalAssetValue = 100000;

        // Page object for UI interactions
        PortfolioPage portfolio = new PortfolioPage(page);
        

        // Get portfolio data
        List<Security> testData = TestDataProvider.testData();

        System.out.println("Starting test with " + testData.size() + " securities");

        // Running the actual API- Service layer calculation  //This can be rest URL 
        List<RebalanceResult> actualResults = RebalanceService.balancePorfolio(testData, totalAssetValue);
        
        HashMap<String, RebalanceResult>  expectedData = TestDataProvider.expectedShareCountMap() ;
        
        List<String> errors = validateResutls(testData, actualResults, expectedData);
        
        if (!errors.isEmpty()) {
			System.out.println("Validation failed with following errors:");
			for (String error : errors) {
				System.out.println("- " + error);
			}
		} else {
			System.out.println("✓ All validations passed!");
		}
    
    }

	private List<String> validateResutls(List<Security> testData, List<RebalanceResult> actualResults,
			HashMap<String, RebalanceResult> expectedData) {
		
		List<String> errors = new ArrayList<String>();
		HashMap<String,RebalanceResult> actualData =  new HashMap();
			
        
        for (int i = 0 ; i < testData.size(); i++) {

        	Security testSec = testData.get(i);
			RebalanceResult expectedAction = expectedData.get(testSec.name);
			
			int matchCount = (int) actualResults.stream().
							filter(r -> r.getShareName().equalsIgnoreCase(testSec.name)).
							count();
			 
			if(matchCount == 0) {  //TC1.1
				errors.add(testSec.name + " is missing in actual results");
				continue; 
			} else if (matchCount > 1) {//TC1.2
				errors.add("Multiple entries found for " + testSec.name + " in actual results");
				continue; 
			}
			
			RebalanceResult actualResult = actualResults.stream().
											 filter(r -> r.getShareName().equalsIgnoreCase(testSec.name)).
											 findFirst().orElse(null);
			
			actualData.put(testSec.name,actualResult) ;
			
	
	       
		      // Validate action (BUY/SELL/NONE)  TC2.1-2.2
	        if (validateBalanceAction(expectedAction.getAction(), actualResult.getAction())) {
	        	
	        } else {
	        	errors.add(testSec.name + " action mismatch: expected " + expectedAction.getAction() + " but got " + actualResult.getAction());
	        }
	        
	    	// Validate shares  //TC2.3
	        if (validateShareCount(expectedAction.getShares(), actualResult.getShares())) {}
	        else {
	        	errors.add(testSec.name + " shares mismatch: expected " + expectedAction.getShares() + " but got " + actualResult.getShares());
	        }
        }
        
        //validate total Assets value
        BigDecimal updateTotalAssetValue = TestDataProvider.calculateUpdatedPortfolioValue(testData, 
        		actualData, 
        		BigDecimal.valueOf(TestDataProvider.totalAssetPrice())); 
        System.out.print("updateTotalAssetValue :"+updateTotalAssetValue);
        if (updateTotalAssetValue.compareTo(
                BigDecimal.valueOf(TestDataProvider.totalAssetPrice())) > 0) {
        	errors.add("Asset Price Croosed to =>" + updateTotalAssetValue.doubleValue()) ;
        }
        
        // Check for any unexpected securities in actual results  -Data integrity check
        for (RebalanceResult actual : actualResults) { //TC3.1
        	if (!expectedData.containsKey(actual.getShareName())) {
				//Assert.fail("Unexpected security in actual results: " + actual.getShareName());
				errors.add("Unexpected security in actual results: " + actual.getShareName());
			}
		}
        
		return errors;
	}
    
    @Test
    public void validateRebalanceBatch() {

        double totalAssetValue = 100000;

        // Page object for UI interactions
        PortfolioPage portfolio = new PortfolioPage(page);

        // Get portfolio data
        List<Security> testData = TestDataProvider.testData();

        System.out.println("Starting test with " + testData.size() + " securities");

        // Running the actual API- Service layer calculation  //This can be rest URL 
        int batchId = RebalanceService.balancePorfolioBatch(testData, totalAssetValue);
        
        List<RebalanceResult> actualResults = RebalanceService.getBatchResults(batchId);
                
        HashMap<String, RebalanceResult>  expectedData = TestDataProvider.expectedShareCountMap() ;
        
        List<String> errors = validateResutls(testData, actualResults, expectedData);
        
        if (!errors.isEmpty()) {
			System.out.println("Validation failed with following errors:");
			for (String error : errors) {
				System.out.println("- " + error);
			}
		} else {
			System.out.println("✓ All validations passed!");
		}
    
    }
    
    @Test
    public void validateRebalanceAPIWithError() {

        double totalAssetValue = 100000;

        // Page object for UI interactions
        PortfolioPage portfolio = new PortfolioPage(page);
        

        // Get portfolio data
        List<Security> testData = TestDataProvider.testData();

        System.out.println("Starting test with " + testData.size() + " securities");

        // Running the actual API- Service layer calculation  //This can be rest URL 
        List<RebalanceResult> actualResults = RebalanceService.balancePorfolioWithError(testData, totalAssetValue);
        
        HashMap<String, RebalanceResult>  expectedData = TestDataProvider.expectedShareCountMap() ;
        
        List<String> errors = validateResutls(testData, actualResults, expectedData);
        
        if (!errors.isEmpty()) {
			System.out.println("Validation failed with following errors:");
			for (String error : errors) {
				System.out.println("- " + error);
			}
		} else {
			System.out.println("✓ All validations passed!");
		}
    
    }
    
    public boolean validateBalanceAction(String expectedAction, String actualAction) {
		if (expectedAction.equalsIgnoreCase("BUY") && actualAction.equalsIgnoreCase("BUY")) {
			return true;
		} else if (expectedAction.equalsIgnoreCase("SELL") && actualAction.equalsIgnoreCase("SELL")) {
			return true;
		} else if (expectedAction.equalsIgnoreCase("NONE") && actualAction.equalsIgnoreCase("NONE")) {
			return true;
		}
		return false;
	}
    /**
     * 
     * why BigDecimal is preferred:

	1. Precision: BigDecimal provides exact precision for decimal numbers, unlike float or double, which can introduce rounding errors due to their binary floating-point representation. For example, 0.1 + 0.2 might not exactly equal 0.3 with double.
	2. Rounding Control: The setScale method allows you to specify the number of decimal places and the rounding mode (e.g., RoundingMode.DOWN). This ensures consistent and predictable rounding behavior.
	3. Comparison: The compareTo method in BigDecimal allows for precise comparison of two numbers, considering their scale and value. This is crucial when validating equality between two decimal numbers.

In your code:

	•  expectedShares and actualShares are converted to BigDecimal to ensure precise rounding to 2 decimal places.
	•  The compareTo method is used to check if the rounded values are equal.

	Using BigDecimal ensures that the validation logic is accurate and free from floating-point errors.
     * 
     * 
     * 
     * @param expectedShares
     * @param actualShares
     * @return
     */
    public boolean validateShareCount(Double expectedShares, Double actualShares) {
    	
    	BigDecimal expectedBD = new BigDecimal(expectedShares);
    	expectedBD = expectedBD.setScale(2, RoundingMode.DOWN);
    	
    	BigDecimal actualSharesBD = new BigDecimal(actualShares);
    	actualSharesBD = actualSharesBD.setScale(2, RoundingMode.DOWN);

    	
        return expectedBD.compareTo(actualSharesBD) == 0;
   }

   
}