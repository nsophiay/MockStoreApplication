package a3;

import javax.jws.WebService;

@WebService
public interface WebInterface {

	  boolean addItem (String managerID, String itemID, String itemName, short quantity, double price);
	  boolean removeItem (String managerID, String itemID, short quantity);
	  String listItemAvailability (String managerID);
	  double purchaseItem (String customerID, String itemID, DSMSApp.Date dateOfPurchase);
	  String findItem (String customerID, String itemName);
	  double returnItem (String customerID, String itemID, DSMSApp.Date dateOfReturn);
	  boolean exchangeItem (String customerID, String newItemID, String oldItemID);
	  
}
