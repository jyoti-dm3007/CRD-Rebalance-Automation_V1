package models;

public class RebalanceResult {

	private String shareName;
    private Double shares;
    private String action; //Buy or Sell or None

    // Constructor for RebalanceResult
    public RebalanceResult(String shareName,Double shares, String action) {
    	this.shareName = shareName; 	//Stock name (IBM, MSFT, etc.)
        this.shares = shares; 			//how many share to buy/sell
        this.action = action; 			//Action BUY/SELL/none
    }

    public Double getShares() {
        return shares;
    }

    public String getAction() {
        return action;
    }
    
    public String getShareName() {
        return shareName;
    }
}