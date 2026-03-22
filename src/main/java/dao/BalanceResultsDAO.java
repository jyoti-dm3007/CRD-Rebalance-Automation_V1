package dao;

import models.RebalanceResult;
import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BalanceResultsDAO {

    /**
     * Insert all rebalance results to database using batch update
     * Creates ONE batch_id for all results in this call
     * All 5 results share the SAME batch_id
     * 
     * @param results List of RebalanceResult to save
     * @return Batch ID (unique identifier for this batch of 5 results)
     */
    public static int saveBatchResults(List<RebalanceResult> results) throws Exception {
        
        Connection conn = DBConnection.getConnection();
        
        try {
            // Step 1: Get the NEXT batch_id (MAX(batch_id) + 1)
            // This ensures we get ONE unique batch_id for all results
            String maxBatchSql = "SELECT COALESCE(MAX(batch_id), 0) + 1 as next_batch_id FROM balance_batch_results";
            
            int batchId = 1;
            try (PreparedStatement maxPs = conn.prepareStatement(maxBatchSql);
                 java.sql.ResultSet rs = maxPs.executeQuery()) {
                if (rs.next()) {
                    batchId = rs.getInt("next_batch_id");
                }
            }
            
            System.out.println("✓ Created Batch ID: " + batchId + " for " + results.size() + " results");
            
            // Step 2: Insert ALL results with the SAME batch_id
            String sql = "INSERT INTO balance_batch_results (batch_id, sec_name, action, share_quantity) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                
                // Add all results to batch with SAME batch_id
                for (RebalanceResult result : results) {
                    ps.setInt(1, batchId);                          // ← SAME batch_id for all 5 results
                    ps.setString(2, result.getShareName());         // sec_name
                    ps.setString(3, result.getAction());            // action
                    ps.setDouble(4, result.getShares());            // share_quantity
                    
                    ps.addBatch();
                }
                
                // Execute batch insert - ALL records inserted at once
                int[] batchResults = ps.executeBatch();
                
                System.out.println("✓ Inserted " + batchResults.length + " records");
    
                
                return batchId;  // ← Return ONE batch_id for all 5 results
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Error saving batch results: " + e.getMessage());
            throw new Exception("Failed to save balance results to database", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Retrieve all rebalance results for a specific batch_id
     * 
     * @param batchId The batch ID to retrieve
     * @return List<RebalanceResult> containing all results for this batch
     * @throws Exception if database error occurs
     */
    public static List<RebalanceResult> getResultsByBatchId(int batchId) throws Exception {
        
        String sql = "SELECT batch_id, sec_name, action, share_quantity, calculated_at " +
                     "FROM balance_batch_results WHERE batch_id = ? ORDER BY sec_name";
        
        List<RebalanceResult> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, batchId);
            
            try (ResultSet rs = ps.executeQuery()) {
                
                // Fetch all results for this batch_id
                while (rs.next()) {
                    String secName = rs.getString("sec_name");
                    String action = rs.getString("action");
                    double shares = rs.getDouble("share_quantity");
                    
                    // Create RebalanceResult object
                    RebalanceResult result = new RebalanceResult(secName, shares, action);
                    results.add(result);
                }
            }
            
            System.out.println("✓ Retrieved " + results.size() + " results for Batch ID: " + batchId);
            
            return results;
            
        } catch (SQLException e) {
            System.out.println("✗ Error retrieving batch results: " + e.getMessage());
            throw new Exception("Failed to retrieve batch results from database", e);
        }
    }
    
    // ...existing code...
    public static boolean saveSingleResult(RebalanceResult result) throws Exception {
        
        String sql = "INSERT INTO balance_batch_results (sec_name, action, share_quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, result.getShareName());
            ps.setString(2, result.getAction());
            ps.setDouble(3, result.getShares());
            
            int rowsInserted = ps.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("✓ Inserted: " + result.getShareName() + " - " + result.getAction() + " - " + result.getShares() + " shares");
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.out.println("✗ Error saving result: " + e.getMessage());
            throw new Exception("Failed to save balance result to database", e);
        }
    }
}
