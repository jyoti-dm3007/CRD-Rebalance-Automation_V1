package pages;

import com.microsoft.playwright.Page;
import core.BasePage;

public class PortfolioPage extends BasePage {

    public PortfolioPage(Page page) {
        super(page);
    }
/*
    // method to get shares with wait
    public long getShares(String securityName) {
        // Using CSS selector - the td has BOTH classes: highlight AND shareQty
        // Correct syntax: td.highlight.shareQty (chained classes)
        // Find the row with data-security attribute, then find the td with both highlight and shareQty classes
        String selector = "tr[data-security=\"" + securityName + "\"] td.highlight.shareQty";
        //System.out.println("Getting shares for " + securityName + " using selector: " + selector);
        String text = getTextWithWait(selector);
        System.out.println("Found shares: " + text);
        return Long.parseLong(text);
    }

    // get action (BUY/SELL/NONE) with wait
    public String getAction(String securityName) {
        // Find the row by data-security, then get the action cell
        // The action cell is the 6th td in the row (after security, target, current, variance, price)
    	
        String selector = "tr[data-security=\"" + securityName + "\"] td:nth-child(6)";
        //System.out.println("Getting action for " + securityName + " using selector: " + selector);
        String text = getTextWithWait(selector);
        System.out.println("Found action: " + text);
        return text;
    }
    */
}