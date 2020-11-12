package a3;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;

@WebServiceRefs({
	@WebServiceRef(wsdlLocation="http://localhost:8080/a3/WebInterfaceQC?wsdl"),
	@WebServiceRef(wsdlLocation="http://localhost:8080/a3/WebInterfaceON?wsdl"),
	@WebServiceRef(wsdlLocation="http://localhost:8080/a3/WebInterfaceBC?wsdl")
})
public class DSMSCustomerClient {

	String customerID;
	double budget;
	String store;
	File log;
	
	DSMSImplService implservice;	
	WebInterface service;

	public DSMSCustomerClient(String[] args, String ID) {
		if(ID.charAt(2) != 'U') {
			System.out.println("User IDs must be in the format: [provinceAcronym]U[4-digit ID]");
			return;
		}
		customerID = ID;
		budget = 1000.00;
		store = ID.substring(0,2);
		try {
			log = new File(ID + "log.txt");
			implservice = new DSMSImplServiceLocator();
			service = implservice.getDSMSImplPort();
			if(log.createNewFile()) {
				System.out.println("File created for customer #" + ID);
			}
		} catch (IOException | ServiceException e) {
			e.printStackTrace();
		}

	}

	public int purchase(String itemID, a3.Date dateOfPurchase) throws RemoteException {

		int status = 1;

		String custAndBudget = this.customerID + budget;

		double price = service.purchaseItem(custAndBudget, itemID, dateOfPurchase);

		if(price > 0 && this.budget > price) { // If purchase was successful, subtract from budget
			this.budget -= price;
		}
		else if(price == 0.0) {
			status = 0;
			System.out.println("User " + this.customerID + " could not purchase item " + itemID);
		}
		else if(price == -1){ // If the item is not in stock, ask the customer if they want to be added to the list
			status = -1;
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);
				writeUser.append("\nREQUEST:\n" + "\tDate of purchase: " + dateOfPurchase
						+ "\n\tType: purchase" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				writeUser.append(status==1?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	public boolean find(String itemName) throws RemoteException {

		boolean nothingFound = true;
		// corba invoke method
		String found = service.findItem(this.customerID,itemName);
		if(!found.equals("")) {
			String[] founds = found.split(",");
			for(String i : founds) {
				i.trim();

				//if(i.matches("[\\s\\[\\]]*[0-9A-Za-z\\s.]+")) {
				System.out.println(i);
				nothingFound = false;
				//}
			}
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: search" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem name: " + itemName);
				writeUser.append(nothingFound?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return !nothingFound;
	}

	public boolean returnItem(String itemID, a3.Date dateOfReturn) throws RemoteException {

		boolean status = true;
		// corba invoke method
		double price = service.returnItem(this.customerID, itemID, dateOfReturn);
		if(price > 0) this.budget+=price;
		else status = false;

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate of return: " + dateOfReturn
						+ "\n\tType: return" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				writeUser.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return status;
	}

	public boolean exchangeItem(String customerID, String newItemID, String oldItemID) {

		boolean status = true;
		String custAndBudget = this.customerID + budget;

		// corba invoke method
		// status = dsmsServant.exchangeItem(custAndBudget, newItemID, oldItemID);


		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate of exchange: " + a3.Date.getCurrentDate()
				+ "\n\tType: return" + "\n\tSubmitted by: " + customerID
				+ "\n\tOld Item ID: " + oldItemID
				+ "\n\tNew Item ID: " + newItemID
						);
				writeUser.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return status;
	}


}
