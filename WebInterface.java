package a3;

import java.util.Date;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface WebInterface {

	  boolean addItem (String managerID, String itemID, String itemName, short quantity, double price);
	  boolean removeItem (String managerID, String itemID, short quantity);
	  String listItemAvailability (String managerID);
	  double purchaseItem (String customerID, String itemID, DSMSApp.Date dateOfPurchase);
	  String findItem (String customerID, String itemName);
	  double returnItem (String customerID, String itemID, DSMSApp.Date dateOfReturn);
	  boolean exchangeItem (String customerID, String newItemID, String oldItemID);
	  
}
