package models;

/*This class helps calculate-Target allocation,Current allocation,Buy/Sell decisions*/

public class Security implements Comparable<Security> {

    public String name;  			//Stock name (IBM, MSFT, etc.)
    public double targetPerAsset;  	//Desired allocation (%)
    public double currentPerAsset;	//Current allocation (%)
    public double unitPrice;		//Price per share
    

    public Security(String name, double target, double current, double price) {   
        this.name = name;
        this.targetPerAsset = target;
        this.currentPerAsset = current;
        this.unitPrice = price;
    } 

    
    public double targetValue(double totalAssetValue) {
        return totalAssetValue * (targetPerAsset / 100);   
    }

    
    public double currentValue(double totalAssetValue) {
        return totalAssetValue * (currentPerAsset / 100);   
    }


	@Override //Compare securities by name for sorting
	public int compareTo(Security o) {
		// TODO Auto-generated method stub
		return this.name.compareTo(o.name);
		
	}
	
	public String toString() {
		return "Sec name :"  + name  + "| targetPerAsset:" + targetPerAsset  +
				 " | currentPerAsset: " + currentPerAsset + " | unitPrice:" + unitPrice + "\n" ;
		
		
	}
    
}


/*	This Security model represents an individual asset in a portfolio.
	It encapsulates allocation logic by calculating target and current values based on total portfolio size.
	This is essential for portfolio rebalancing, where we determine buy/sell actions based on allocation drift.

  	public Security(String name, double target, double current, double price) { 
    Constructor-Used to create a stock object with the specified name, target allocation, current allocation and price per share.

	//like Security ibm = new Security("IBM", 20, 10, 150);
	currentValue = 100,000 * (10/100) = 10,000 IBM
	targetValue = 100,000 * (20/100) = 20,000 IBM

	IBM Differenece between targetValue and currentValue is that targetValue calculates the desired allocation of the stock 
	20,000 - 10,000 = +10,000 (need to buy more) while currentValue calculates the current allocation of the stock in the portfolio.

	Shares to Buy  = 10,000 / 150 = 66.67 ≈ 67 shares

*/