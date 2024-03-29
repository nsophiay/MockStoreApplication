package a3;

import java.io.File; 
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;

public class DSMSManagerClient {

	String managerID;
	String store;
	File log;
	
	WebInterface service;
	
	public DSMSManagerClient(String[] args, String ID) {
		if(ID.charAt(2) != 'M') {
			System.out.println("Manager IDs must be in the format: [provinceAcronym]M[4-digit ID]");
			return;
		}
		managerID = ID;
		store = ID.substring(0,2);
		try {
			log = new File(ID + "log.txt");
			QName compQName = new QName("http://a3/", "DSMSImplService");
			if(store.equals("QC")) {
				URL compURL = new URL("http://localhost:8080/a3/WebInterfaceQC?wsdl");
				Service compService = Service.create(compURL, compQName);
				service = compService.getPort(WebInterface.class);
			} else if(store.equals("ON")) {
				URL compURL = new URL("http://localhost:8080/a3/WebInterfaceON?wsdl");
				Service compService = Service.create(compURL, compQName);
				service = compService.getPort(WebInterface.class);
			}
			else if(store.equals("BC")) {
				URL compURL = new URL("http://localhost:8080/a3/WebInterfaceBC?wsdl");
				Service compService = Service.create(compURL, compQName);
				service = compService.getPort(WebInterface.class);
			}
			if(log.createNewFile()) {
				System.out.println("File created for manager #" + ID);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// corba stuff used to be here


	}


	public boolean add(String itemID, String itemName, short quantity, double price) throws RemoteException {

		boolean status = false;

		// corba invoke method
		status = service.addItem(this.managerID, itemID, itemName, quantity, price);


		// Write to file
		synchronized(log) {
			try {
				FileWriter writeManager = new FileWriter(log, true);
				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: add" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem name: " + itemName
						+ "\n\tItem quantity: " + quantity
						+ "\n\tItem price: " + price);
				writeManager.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeManager.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	public boolean remove(String itemID, short quantity) throws RemoteException {
		boolean status = false;

		// corba invoke method
		status = service.removeItem(this.managerID, itemID, quantity);

		// Write to file
		synchronized(log) {
			try {

				FileWriter writeManager = new FileWriter(log, true);
				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: remove" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem quantity: " + quantity);
				writeManager.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return status;
	}
	public void list() throws RemoteException {

		// corba invoke method
		System.out.println(service.listItemAvailability(this.managerID));

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeManager = new FileWriter(log, true);

				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: list inventory" + "\n\tSubmitted by: " + managerID);
				writeManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}

	}


}
