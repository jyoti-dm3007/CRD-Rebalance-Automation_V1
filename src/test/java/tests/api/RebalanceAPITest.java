package tests.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RebalanceAPITest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String API_PATH = "/api/rebalance";
    private int batchId = -1;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        System.out.println("RestAssured configured for: " + BASE_URL);
    }

    /**
     * Test 1: Calculate Only (No Database Save)
     * POST /api/rebalance/calculate
     */
    @Test(priority = 1)
    public void testCalculateOnly() {
        System.out.println("\n TEST 2: Calculate Rebalance (No DB Save)");
        System.out.println("POST " + API_PATH + "/calculate");

        String requestBody = "{ \"totalAssetValue\": 100000 }";

        Response response = given()
            .log().all()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post(API_PATH + "/calculate")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("success", equalTo(true))
            .body("message", containsString("calculated successfully"))
            .body("results", hasSize(5))
            .body("results[0].shareName", notNullValue())
            .body("results[0].action", anyOf(equalTo("BUY"), equalTo("SELL"), equalTo("NONE")))
            .body("results[0].shares", notNullValue())
            .extract()
            .response();

        // Verify all 5 securities are present
        List<Map<String, Object>> results = response.jsonPath().getList("results");
        Assert.assertEquals(results.size(), 5, "Expected 5 results");

        System.out.println("Check the Results:");
        for (Map<String, Object> result : results) {
            System.out.println("  " + result.get("shareName") + " - " + 
                             result.get("action") + " - " + 
                             result.get("shares") + " shares");
        }

        System.out.println("Test-Calculate Rebalance (No DB Save PASSED");
    }

    /**
     * Test 2: Calculate and Save to Database (Creates Batch)
     * POST /api/rebalance/calculate-batch
     */
    @Test(priority = 2)
    public void testCalculateAndSaveBatch() {
        System.out.println("\n TEST 3: Calculate and Save Batch");
        System.out.println("POST " + API_PATH + "/calculate-batch");

        String requestBody = "{ \"totalAssetValue\": 100000 }";

        Response response = given()
            .log().all()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post(API_PATH + "/calculate-batch")
        .then()
            .log().all()
            .extract()
            .response();

        int statusCode = response.getStatusCode();
        if (statusCode == 500) {
            System.err.println("Server error occurred: " + response.getBody().asString());
            Assert.fail("Server error: 500");
        } else if (statusCode != 200) {
            System.err.println("Unexpected status code: " + statusCode);
            Assert.fail("Unexpected status code: " + statusCode);
        }

        // Validate response body for success
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "API did not return success");
        Assert.assertTrue(response.jsonPath().getString("message").contains("successfully"), "Message does not indicate success");

        // Extract batch ID for next test
        batchId = response.jsonPath().getInt("batchId");
        Assert.assertTrue(batchId > 0, "Batch ID is not valid");

        System.out.println(" Batch ID created: " + batchId);
        System.out.println("Test-Calculate and Save Batch into DB PASSED");
    }

    /**
     * Test 3: Get Results by Batch ID
     * GET /api/rebalance/batch/{batchId}
     */
    @Test(priority = 3, dependsOnMethods = "testCalculateAndSaveBatch")
    public void testGetResultsByBatchId() {
        System.out.println("\n TEST 4: Get Results by Batch ID");
        System.out.println("GET " + API_PATH + "/batch/" + batchId);

        Response response = given()
            .log().all()
            .pathParam("batchId", batchId)
        .when()
            .get(API_PATH + "/batch/{batchId}")
        .then()
            .log().all()
            .statusCode(200)
            .contentType(ContentType.JSON)
            //.header("Authorization", "Bearer <your_token>")
            .body("success", equalTo(true))
            .body("batchId", equalTo(batchId))
            .body("results", hasSize(5))
            .extract()
            .response();

        List<Map<String, Object>> results = response.jsonPath().getList("results");
        Assert.assertEquals(results.size(), 5, "Expected 5 results in batch");

        System.out.println("Rebalance Retrieved from DB-table " + results.size() + " results from batch " + batchId);
        System.out.println("✓ Results:");
        for (Map<String, Object> result : results) {
            System.out.println("  " + result.get("shareName") + " - " + 
                             result.get("action") + " - " + 
                             result.get("shares") + " shares");
        }

        System.out.println("Test-Get Results by Batch ID from DB-table PASSED");
    }

    /**
     * Test 4: Verify IBM Stock Rebalance Action
     * GET /api/rebalance/batch/{batchId} -> Filter IBM
     */
    @Test(priority = 4, dependsOnMethods = "testCalculateAndSaveBatch")
    public void testIBMRebalanceAction() {
        System.out.println("\n TEST 5: Verify IBM Stock Rebalance");
        System.out.println("GET " + API_PATH + "/batch/" + batchId + " -> IBM");

        Response response = given()
            .log().all()
            .pathParam("batchId", batchId)
        .when()
            .get(API_PATH + "/batch/{batchId}")
        .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response();

        List<Map<String, Object>> results = response.jsonPath().getList("results");

        // Find IBM result
        Map<String, Object> ibm = results.stream()
            .filter(r -> "IBM".equals(r.get("shareName")))
            .findFirst()
            .orElse(null);

        Assert.assertNotNull(ibm, "IBM result not found");
        Assert.assertEquals(ibm.get("action"), "BUY", "IBM action should be BUY");
        Assert.assertTrue(validateShareCount(((Number) ibm.get("shares")).doubleValue(), 66.665d), "IBM shares should be 66.66");

       
        //System.out.println(" IBM: " + ibm.get("action") + " " + ibm.get("shares") + " shares","IBM shares should be 67");
        System.out.println("Test-Verify IBM Stock Rebalance Action PASSED");
    }
    
    public boolean validateShareCount(Double expectedShares, Double actualShares) {
    	
    	System.out.println("expectedShares " +expectedShares) ;
    	System.out.println("actualShares  "+actualShares) ;

    	
    	BigDecimal expectedBD = new BigDecimal(expectedShares);
    	expectedBD = expectedBD.setScale(2, RoundingMode.DOWN);
    	
    	BigDecimal actualSharesBD = new BigDecimal(actualShares);
    	actualSharesBD = actualSharesBD.setScale(2, RoundingMode.DOWN);

    	
        return expectedBD.compareTo(actualSharesBD) == 0;
   }

    /**
     * Test 5: Validate All Securities Present
     * GET /api/rebalance/batch/{batchId}
     */
    @Test(priority = 5, dependsOnMethods = "testCalculateAndSaveBatch")
    public void testAllSecuritiesPresent() {
        System.out.println("\n TEST 6: Validate All Securities Present");
        System.out.println("GET " + API_PATH + "/batch/" + batchId);

        Response response = given()
            .log().all()
            .pathParam("batchId", batchId)
        .when()
            .get(API_PATH + "/batch/{batchId}")
        .then()
            .log().all()
            .statusCode(200)
            .extract()
            .response();

        List<Map<String, Object>> results = response.jsonPath().getList("results");

        // Verify all 5 securities
        String[] expectedSecurities = {"AAPL", "HD", "IBM", "MSFT", "ORCL"};
        for (String security : expectedSecurities) {
            boolean found = results.stream()
                .anyMatch(r -> security.equals(r.get("shareName")));
            Assert.assertTrue(found, "Security " + security + " not found");
            System.out.println("✓ " + security + " found");
        }

        System.out.println("Test-Validate All Securities Present PASSED");
    }

    

  
   
}