package services;

import models.Security;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dao.BalanceResultsDAO;
import db.DBConnection;
import models.RebalanceResult;

public  class RebalanceService {

    public static RebalanceResult calculate(Security sec,double totalAssetValue) {

        double targetValue = totalAssetValue * sec.targetPerAsset/100;
        double currentValue = totalAssetValue * sec.currentPerAsset/100;

        double differenceValue = targetValue - currentValue;

        double shares = Math.abs(differenceValue / sec.unitPrice);

        String action;

        if (differenceValue > 0) action = "BUY";
        else if (differenceValue < 0) action = "SELL";
        else action = "NONE";

        return new RebalanceResult(sec.name ,shares, action);
    }
    
    public static List<RebalanceResult> balancePorfolio(List<Security> securities,double totalAssetValue) {
   	
    	List<RebalanceResult> results = new ArrayList<RebalanceResult>();
    	for (Security sec : securities) {
   			results.add(calculate(sec, totalAssetValue));
		}
    	return results;
    }
    
    
    
    
 public static int balancePorfolioBatch(List<Security> securities,double totalAssetValue) {
    	
    	List<RebalanceResult> results = balancePorfolio(securities, totalAssetValue);
    	    		
    	try {
		System.out.println("Saving " + results.size() + " rebalance results to database...");
		
		// Use DAO layer to save the results to DB and get batch ID
		int batchId = BalanceResultsDAO.saveBatchResults(results);
		
		System.out.println("✓ Successfully saved with Batch ID: " + batchId);
		
		return batchId;  // Return batch ID
		
	} catch (Exception e) {
		System.out.println("✗ Error saving results: " + e.getMessage());
		e.printStackTrace();
		return -1;  // Return -1 on error
	}

}
 
 public static List<RebalanceResult> getBatchResults(int batchId) {
		
		try {
			System.out.println("Retrieving results for Batch ID: " + batchId);
			
			// Use DAO layer to fetch results by batch ID
			List<RebalanceResult> results = BalanceResultsDAO.getResultsByBatchId(batchId);
			
			System.out.println("✓ Successfully retrieved " + results.size() + " results for Batch ID: " + batchId);
			
			return results;
			
		} catch (Exception e) {
			System.out.println("✗ Error retrieving results: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();  // Return empty list on error
		}
	}
 
 
 public static RebalanceResult calculateWithError(Security sec,double totalAssetValue) {

     double targetValue = totalAssetValue * sec.targetPerAsset/100;
     double currentValue = totalAssetValue * sec.currentPerAsset/100;

     double differenceValue = targetValue - currentValue ; 

     double shares = Math.abs(differenceValue / sec.unitPrice+1);

     String action;

     if (differenceValue > 0) action = "BUY";
     else if (differenceValue < 0) action = "SELL";
     else action = "NONE";

     return new RebalanceResult(sec.name ,shares, action);
 }
 
 public static List<RebalanceResult> balancePorfolioWithError(List<Security> securities,double totalAssetValue) {
	
 	List<RebalanceResult> results = new ArrayList<RebalanceResult>();
 	for (Security sec : securities) {
			results.add(calculateWithError(sec, totalAssetValue));
		}
 	return results;
 }
 
 
 
}