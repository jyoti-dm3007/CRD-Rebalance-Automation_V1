package base;

import core.PlaywrightFactory;
import com.microsoft.playwright.Page;
import org.testng.annotations.*;

public class BaseTest {

    protected Page page;
    private static final int PAGE_LOAD_TIMEOUT = 30000; // 30 seconds

    @BeforeMethod
    @Parameters({"baseUrl"})
    public void setup(@Optional String baseUrl) {
        System.out.println("Received baseUrl: " + baseUrl);
        
        page = PlaywrightFactory.initBrowser();
               
        // Set default timeout for all page operations
        page.setDefaultTimeout(PAGE_LOAD_TIMEOUT);
       
        
        // Navigate to the URL if baseUrl is provided
        if (baseUrl != null && !baseUrl.isEmpty()) {
                        
            try {
                page.navigate(baseUrl);
                               
                // Wait for page to load completely
                page.waitForLoadState();
                               
                // Debug: Print page content info
                System.out.println("Page title: " + page.title());
                System.out.println("Page URL: " + page.url());
                
                // Get page content to debug
                String content = page.content();
                               
                // Try to find table - be lenient, just try to find any table
                try {
                    page.waitForSelector("table", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
                    } catch (Exception e) {
                   }
                              
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Setup failed: " + e.getMessage(), e);
            }
            System.out.println("==========================\n");
        } else {
            System.out.println(" BaseUrl parameter not provided - skipping navigation");
        }
        System.out.println("===== SETUP COMPLETE =====\n");
    }

    @AfterMethod
    public void tearDown() {
        PlaywrightFactory.close();
    }
}