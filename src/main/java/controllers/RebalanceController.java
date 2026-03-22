package controllers;

import org.springframework.web.bind.annotation.*;
import services.RebalanceService;
import models.RebalanceResult;
import models.Security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/rebalance")
public class RebalanceController {

    /**
     * Get portfolio data (5 securities)
     */
    private List<Security> getPortfolioData() {
        return Arrays.asList(
                new Security("IBM", 20, 10, 150),
                new Security("MSFT", 20, 20, 90),
                new Security("ORCL", 20, 30, 220),
                new Security("AAPL", 20, 20, 450),
                new Security("HD", 20, 20, 70)
        );
    }

    /**
     * Calculate and save rebalance results to database
     * Creates ONE batch_id for all 5 results
     * 
     * POST /api/rebalance/calculate-batch
     * Body: {
     *   "totalAssetValue": 100000
     * }
     */
    @PostMapping("/calculate-batch")
    public RebalanceResponse calculateAndSaveBatch(@RequestBody RebalanceRequest request) {
        
        System.out.println("📊 POST /api/rebalance/calculate-batch");
        System.out.println("   Total Asset Value: " + request.getTotalAssetValue());
        
        try {
            // Get portfolio data (5 securities)
            List<Security> securities = getPortfolioData();
            
            // Calculate and save to database
            int batchId = RebalanceService.balancePorfolioBatch(securities, request.getTotalAssetValue());
            
            if (batchId > 0) {
                return new RebalanceResponse(
                    true,
                    "Rebalance calculated and saved successfully",
                    batchId,
                    null
                );
            } else {
                return new RebalanceResponse(
                    false,
                    "Failed to save rebalance results",
                    -1,
                    null
                );
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return new RebalanceResponse(
                false,
                "Error: " + e.getMessage(),
                -1,
                null
            );
        }
    }

    /**
     * Get all rebalance results for a specific batch_id
     * 
     * GET /api/rebalance/batch/1
     */
    @GetMapping("/batch/{batchId}")
    public RebalanceResponse getResultsByBatch(@PathVariable int batchId) {
        
        System.out.println("📊 GET /api/rebalance/batch/" + batchId);
        
        try {
            // Retrieve results from database
            List<RebalanceResult> results = RebalanceService.getBatchResults(batchId);
            
            if (results != null && !results.isEmpty()) {
                return new RebalanceResponse(
                    true,
                    "Results retrieved successfully",
                    batchId,
                    results
                );
            } else {
                return new RebalanceResponse(
                    false,
                    "No results found for batch ID: " + batchId,
                    batchId,
                    results
                );
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return new RebalanceResponse(
                false,
                "Error: " + e.getMessage(),
                batchId,
                null
            );
        }
    }

    /**
     * Calculate rebalance (without saving to database)
     * 
     * POST /api/rebalance/calculate
     * Body: {
     *   "totalAssetValue": 100000
     * }
     */
    @PostMapping("/calculate")
    public RebalanceResponse calculateOnly(@RequestBody RebalanceRequest request) {
        
        System.out.println("📊 POST /api/rebalance/calculate");
        System.out.println("   Total Asset Value: " + request.getTotalAssetValue());
        
        try {
            // Get portfolio data
            List<Security> securities = getPortfolioData();
            
            // Calculate only (no database save)
            List<RebalanceResult> results = RebalanceService.balancePorfolio(securities, request.getTotalAssetValue());
            
            return new RebalanceResponse(
                true,
                "Rebalance calculated successfully",
                0,  // No batch_id since not saved
                results
            );
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return new RebalanceResponse(
                false,
                "Error: " + e.getMessage(),
                0,
                null
            );
        }
    }

    /**
     * Health check endpoint
     * 
     * GET /api/rebalance/health
     */
    @GetMapping("/health")
    public HealthResponse healthCheck() {
        System.out.println("✓ Health check");
        return new HealthResponse(true, "Rebalance API is up and running");
    }

    // Inner classes for request/response

    /**
     * Request body for rebalance calculations
     */
    public static class RebalanceRequest {
        private double totalAssetValue;

        public RebalanceRequest() {}

        public RebalanceRequest(double totalAssetValue) {
            this.totalAssetValue = totalAssetValue;
        }

        public double getTotalAssetValue() {
            return totalAssetValue;
        }

        public void setTotalAssetValue(double totalAssetValue) {
            this.totalAssetValue = totalAssetValue;
        }
    }

    /**
     * Response body for rebalance operations
     */
    public static class RebalanceResponse {
        private boolean success;
        private String message;
        private int batchId;
        private List<RebalanceResult> results;

        public RebalanceResponse(boolean success, String message, int batchId, List<RebalanceResult> results) {
            this.success = success;
            this.message = message;
            this.batchId = batchId;
            this.results = results;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getBatchId() {
            return batchId;
        }

        public void setBatchId(int batchId) {
            this.batchId = batchId;
        }

        public List<RebalanceResult> getResults() {
            return results;
        }

        public void setResults(List<RebalanceResult> results) {
            this.results = results;
        }
    }

    /**
     * Health check response
     */
    public static class HealthResponse {
        private boolean healthy;
        private String message;

        public HealthResponse(boolean healthy, String message) {
            this.healthy = healthy;
            this.message = message;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public String getMessage() {
            return message;
        }
    }
}
