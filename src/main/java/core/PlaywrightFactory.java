package core;

import com.microsoft.playwright.*;

public class PlaywrightFactory {  //Browser Engine

    private static Playwright playwright;
    private static Browser browser;

    public static Page initBrowser() {

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true) //Headless or Headed mode control
        );
               
        return browser.newPage();
    }
      
    public static void close() {
    /* Managing the resources. It prevents memory leaks,hanging browser sessions */
        browser.close();
        playwright.close();
    }
}



























/*
Environment Control + ThreadLocal (Parallel Execution Matter) + your current project 

package core;

import com.microsoft.playwright.*;

public class PlaywrightFactory {

    private static ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static ThreadLocal<Page> page = new ThreadLocal<>();

    public static Page initBrowser() {

       
        String browserType = System.getProperty("browser", "chromium");  // Get browser from system property (default = chromium)

        playwright.set(Playwright.create());

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(false);

        switch (browserType.toLowerCase()) {

            case "chrome":
            case "chromium":
                browser.set(playwright.get().chromium().launch(options));
                break;

            case "firefox":
                browser.set(playwright.get().firefox().launch(options));
                break;

            case "webkit":
                browser.set(playwright.get().webkit().launch(options));
                break;

            default:
                throw new RuntimeException("Invalid browser: " + browserType);
        }

        page.set(browser.get().newPage());
        return page.get();
    }

    public static Page getPage() {
        return page.get();
    }

    public static void closeBrowser() {
        browser.get().close();
        playwright.get().close();
    }
}

*/