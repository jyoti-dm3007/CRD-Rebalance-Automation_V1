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


