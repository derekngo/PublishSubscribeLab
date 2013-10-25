import java.io.Serializable;

public class Item implements Serializable {
    private int uniqueID;
    private String name;
    private String attributes;
    private double minBid;
    private int seller;
    private static int counter = 1;
    //Constructor that will store the necessary characteristics all item must have in the local variables.
    public Item (int seller,String name, String attributes, double minBid){
    		this.seller = seller;
            this.name = name;
            this.attributes = attributes;
            this.minBid = minBid;
            uniqueID = counter;
            counter++;
    }
    
    public Item(String name, String attributes, double minBid){
    	this.seller = -1;
    	this.name = name;
    	this.attributes = attributes;
    	this.minBid = minBid;
    	this.uniqueID = -1;
    }

    public int getUniqueID(){
            return uniqueID;
    }
    
    public String getName(){
        	return name;
    }
    
    public String getAttributes(){
            return attributes;
    }
    
    public double getMinBid(){
            return minBid;
    }
    
    public int getSeller(){
    	return seller;
    }
    
    public boolean compareMatch(Item i){
    	return this.name.equals(i.getName());
    }
}