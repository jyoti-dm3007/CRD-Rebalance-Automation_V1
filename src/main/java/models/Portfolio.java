package models;

import java.util.List;

public class Portfolio {

    public List<Security> securities;   //List of security objects
    public double totalAsset;			//Total value of the portfolio 100,000

    public Portfolio(List<Security> securities, double totalAsset) {
        this.securities = securities;		//Store all stocks inside this portfolio
        this.totalAsset = totalAsset;		//Store total portfolio value
    }
}

