package core;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class BasePage {

    protected Page page;
    private static final int DEFAULT_TIMEOUT = 15000; // 15 seconds

    public BasePage(Page page) {
        this.page = page;
    }

    // Wait for element to be visible
    public void waitForElementVisible(String selector) {
        
        try {
            page.locator(selector).waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT));
            System.out.println("Element found!");
        } catch (Exception e) {
            System.out.println("Failed to find element: " + e.getMessage());
            throw e;
        }
    }

    // Wait for element to be visible and get text
    public String getTextWithWait(String selector) {
        waitForElementVisible(selector);
        String text = page.locator(selector).textContent();
        if (text != null) {
            text = text.trim();
        }
        return text;
    }

    // Wait for element and click
    public void clickWithWait(String selector) {
        waitForElementVisible(selector);
        page.click(selector);
    }

    // Wait for element and fill
    public void typeWithWait(String selector, String value) {
        waitForElementVisible(selector);
        page.fill(selector, value);
    }

    // Legacy methods (without wait)
    public void click(String locator) {
        page.click(locator);
    }

    public void type(String locator, String value) {
        page.fill(locator, value);
    }

    public String getText(String locator) {
        return page.textContent(locator).trim();
    }
}