package a3;

import java.io.File; 
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

@WebService(endpointInterface = "a3.WebInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class DSMSImpl implements WebInterface{

	HashMap<String,Item> inventory = new HashMap<>();
	File log;
	private static final java.lang.Object lock = new java.lang.Object();
	static Integer sema = new Integer(0);
	String storeName;
	int port;
	static ArrayList<String> itemsFound = new ArrayList<String>();
	private static boolean exchange;
	boolean returnStatus;
	double purchaseStatus;

	public DSMSImpl() {}

	public DSMSImpl(String storeName){

		inventory = new HashMap<String,Item>();
		this.storeName = storeName;
		exchange = false;
		returnStatus = true;

		switch(storeName) {
		case "ON": port = 9000; break;
		case "BC": port = 7000; break;
		case "QC": port = 8000; break;
		default: port = 8000; break;
		}

		Runnable UDPServer = () -> { receive(); };
		Thread UDPthread = new Thread(UDPServer);
		UDPthread.start();


		// Create log file
		try {
			log = new File(storeName + "Serverlog.txt");
			if(log.createNewFile()) {
				System.out.println("File created for " + storeName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public DSMSImpl(String storeName, HashMap<String,Item> inventory){

		this.inventory = inventory;
		this.storeName = storeName;
		exchange = false;
		returnStatus = true;

		switch(storeName) {
		case "ON": port = 9000; break;
		case "BC": port = 7000; break;
		case "QC": port = 8000; break;
		default: port = 8000; break;
		}
		Runnable UDPServer = () -> { receive(); };
		Thread UDPthread = new Thread(UDPServer);
		UDPthread.start();

		// Create log file
		try {
			log = new File(storeName + "Serverlog.txt");
			if(log.createNewFile()) {
				System.out.println("File created for " + storeName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}


	////////////////////////
	// Manager operations //
	////////////////////////

	@WebMethod()
	public boolean addItem(String managerID, String itemID, String itemName, short quantity, double price) {

		boolean status = true;

		// A manager can only add items from their own store
		if(!itemID.substring(0,2).equals(storeName)) {
			status = false;
		}
		else {
			if(inventory.containsKey(itemID)) { // If item already exists, change quantity
				Item i = inventory.get(itemID);
				i.quantity += quantity;
			}
			else { // Otherwise add to hash map
				inventory.put(itemID, new Item(itemID, itemName, quantity, price));
			}
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter write = new FileWriter(log, true);

				write.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: add" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem name: " + itemName
						+ "\n\tItem quantity: " + quantity
						+ "\n\tItem price: " + price);
				write.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}

		return status;
	}

	@WebMethod()
	public boolean removeItem(String managerID, String itemID, short quantity) {

		boolean status = true;

		if(managerID.charAt(2)!='M') {
			status = false;
			System.out.println("Sorry, only managers can remove items!");
		}

		// Check if item exists
		if(inventory.containsKey(itemID)) {
			synchronized(inventory) {
				if(quantity == -1) { // Remove the item entirely if -1 is entered
					// Clear the list of people who have bought the item
					if(!inventory.get(itemID).IDOfBuyer.isEmpty()) {
						inventory.get(itemID).IDOfBuyer.clear();
						inventory.get(itemID).dateOfPurchase = null;
					}
					inventory.remove(itemID); // Remove from inventory
				}
				else if(inventory.get(itemID).quantity > 0 && inventory.get(itemID).quantity >= quantity){ // Otherwise decrement by the specified quantity
					Item i = inventory.get(itemID);
					i.quantity -= quantity;
				}
				else {
					status = false;
				}
			}
		}
		else{
			status = false;
		}
		System.out.println(status?"Remove operation was successful":"Remove operation was not successful");

		// Write to file
		synchronized(log) {
			try {
				FileWriter write = new FileWriter(log, true);

				write.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: remove" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem quantity: " + quantity);
				write.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return status;

	}

	@WebMethod()
	public String listItemAvailability(String managerID) {
		if(managerID.charAt(2)!='M') {
			return "Sorry, only managers can list items!";
		}

		String list = "";

		// List items
		for(Item i : inventory.values()) {
			list += "ID: " + i.itemID + "\nName: " + i.itemName + "\nPrice: " + i.price + "\nQuantity: " + i.quantity + "\n\n";
		}

		// Write to file
		synchronized(log){
			try {
				FileWriter write = new FileWriter(log, true);
				write.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: list inventory" + "\n\tSubmitted by: " + managerID);
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}

		return list;
	}

	////////////////////////
	//   User operations  //
	////////////////////////

	@WebMethod()
	public boolean checkPurchase(String customerID) {
		for(Item item : inventory.values()) {
			if(item.IDOfBuyer.contains(customerID)) return false;
		}
		return true;
	}

	@WebMethod()
	public double purchaseItem(String customerID, String ID, a3.Date dateOfPurchase) {

		String itemID = ID.substring(0,6);
		String store = itemID.substring(0,2);
		Runnable r = () -> {checkAvailability(itemID);};
		Thread queueThread = new Thread(r);
		boolean status = true;
		Item i = null;

		// Fail conditions
		if(customerID.charAt(2)!='U') {
			status = false;
			System.out.println("Sorry, only customers can purchase items!");
		}

		// If the item is not from the store, send a request to the right server
		if(!store.equals(storeName)) {
			return Double.parseDouble(sendPurchaseRequest(store, customerID, ID, dateOfPurchase.getDay(), dateOfPurchase.getMonth(), dateOfPurchase.getYear()));
		}

		// Check if item exists in the store
		if(inventory.containsKey(itemID)) { 

			i = inventory.get(itemID);

			synchronized(i) {

				// Buyers from other stores can only purchase one item
				if(!customerID.substring(0,2).equals(storeName) && !checkPurchase(customerID.substring(0, 7))) {
					System.out.println("This user has already purchased an item from this store.");
					status = false;
				}
				else if(ID.length() > 6 && ID.substring(7).equals("wait")) { // Handle waiting list
					synchronized(i.waitingList) {
						i.waitingList.put(customerID); // Add user to queue
						System.out.println("User " + customerID.substring(0,7) + " added to queue. Current users in queue:");
						for(String id : i.waitingList) {
							System.out.println(id);
						}
					}
					queueThread.start();
				}
				else if(i.quantity > 0) { // Make purchase

					if(customerID.length() > 7 && customerID.substring(7).matches("[0-9.]+")) { // Check budget
						double budget = Double.parseDouble(customerID.substring(7));
						if(i.price > budget) {
							status = false;
						}else {
							synchronized(lock) {
								i.quantity--;
								i.dateOfPurchase = dateOfPurchase;
								i.IDOfBuyer.add(customerID.substring(0,7));
							}
						}
					}


				}
				else { status = false; return -1; }; // Ask the user whether they want to be added to the wait list
			}
		} else{
			System.out.println("Item does not exist.");
			status = false; // Cannot make the purchase because item does not exist
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter write = new FileWriter(log, true);
				write.append("\nREQUEST:\n" + "\tDate of purchase: " + dateOfPurchase
						+ "\n\tType: purchase" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				write.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(status) return i.price; // Returns price if successful
		else return 0.0; // Returns 0 if failed

	}

	@WebMethod()
	// Once an item becomes available, the first customer in the queue will be able to purchase it
	public void checkAvailability(String itemID){

		if(inventory.containsKey(itemID)) {
			Item item = inventory.get(itemID);
			while(!item.waitingList.isEmpty()) {
				synchronized(lock) {
					if(item.quantity > 0) {
						System.out.println("Item " + itemID + " is now in stock");
						String customerID = item.waitingList.poll();
						System.out.println(customerID.substring(0,7) + " is no longer in the waiting list for item " + itemID);

						if(purchaseItem(customerID, itemID, a3.Date.getCurrentDate()) > 0) {
							System.out.println(customerID.substring(0,7) + " has purchased " + itemID);
						}
						else {
							System.out.println(customerID.substring(0,7) + " could not purchase " + itemID);
						}

					}
				}
			}
		}
	}


	@WebMethod()
	public String findItem(String customerID, String itemName) {
		boolean status = true;
		String items = "";

		if(customerID.charAt(2)!='U') {
			status = false;
			return "Sorry, only customers can find items!";
		}

		// Search for item in own store
		for(Item a : inventory.values()) {
			if(a.itemName.equalsIgnoreCase(itemName)) {
				itemsFound.add(a.itemID + " " + a.quantity + " " + a.price + "\n");
			}
		}

		if(customerID.substring(0,2).equals(storeName)) { // Send request to other stores
			for(int i = 7000; i <= 9000; i+=1000) {
				if(i != this.port) {
					sendFindItemRequest(i, customerID, itemName);
				}
			}
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter write = new FileWriter(log, true);
				write.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: search" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem name: " + itemName);
				write.append(status?"\n\tFound items: " + items.toString() + "\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		if(itemsFound.isEmpty()) return "";
		String finalr = itemsFound.toString();
		System.out.println("Find result: " + finalr);

		itemsFound.clear();
		return finalr;
	}

	@WebMethod()
	public double returnItem(String customerID, String itemID, a3.Date dateOfReturn) {
		boolean status = true;
		Item i = null;

		if(itemID.substring(0,2).equals(storeName) && customerID.charAt(2) == 'U') {

			if(inventory.containsKey(itemID)) {
				i = inventory.get(itemID);
				synchronized(i) {
					if(i.IDOfBuyer.contains(customerID)){

						Calendar c = Calendar.getInstance();

						c.set(i.dateOfPurchase.getDay(), i.dateOfPurchase.getMonth(), i.dateOfPurchase.getYear());
						int dayOfYear = c.get(Calendar.DAY_OF_YEAR);

						c.set(dateOfReturn.getDay(), dateOfReturn.getMonth(), dateOfReturn.getYear());
						int dayOfYearReturn = c.get(Calendar.DAY_OF_YEAR);

						// If the item was purchased by this customer within the past 30 days, they can return it
						if((dayOfYearReturn - dayOfYear) < 30 && (dayOfYearReturn-dayOfYear) >= 0){
							i.quantity++;
							i.IDOfBuyer.remove(customerID);
							//i.dateOfPurchase = null;
							return i.price;
						}
						else{
							status = false;
							System.out.println("Sorry, returns are only valid for 30 days after the date of purchase.");
						}
					} else {
						status = false;
						System.out.println("Sorry, " + itemID + " has not been purchased by " + customerID + " yet.");
					}
				}
			}
			else {
				status = false;
			}

		}
		else if(!itemID.substring(0,2).equals(storeName)) {
			String returnResult = sendReturnRequest(customerID, itemID).trim();
			double res = Double.parseDouble(returnResult);

			if(res == 0.0){
				status=false;
			} else return res;
		}
		else {
			status = false;
			System.out.println("Sorry, only customers can return items!");
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter write = new FileWriter(log, true);

				write.append("\nREQUEST:\n" + "\tDate of return: " + dateOfReturn
						+ "\n\tType: return" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				write.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				write.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}

		return 0.0; // Returns 0 if failed
	}

	@WebMethod()
	public boolean exchangeItem(String customerID, String newItemID, String oldItemID) {

		double budget = 0;
		if(customerID.length() > 7) budget = Double.parseDouble(customerID.substring(7));
		String customerIDactual = customerID.substring(0,7);

		a3.Date currentDate = a3.Date.getCurrentDate();
		sema = 2;
		setExchange(true);
		boolean status = false;
		String pStatus = null, rStatus = null;

		pStatus = sendExchangeRequest(customerID, newItemID, "purchase"); // Verify purchase
		rStatus = sendExchangeRequest(customerIDactual, oldItemID, "return"); // Verify return

		if(pStatus.trim().equals("true") && rStatus.trim().matches("[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}")) {
			String[] d = rStatus.trim().split("-");
			a3.Date returnDate = new a3.Date(Short.parseShort(d[0]),Short.parseShort(d[1]),Short.parseShort(d[2]));
			returnItem(customerIDactual, oldItemID, returnDate);
			purchaseItem(customerID, newItemID, currentDate);
			return true;
		} else return false;

	}

	private String sendExchangeRequest(String customerID, String itemID, String type) {

		String store = itemID.substring(0,2);
		int serverPort = 0;

		double budget = 0;

		if(customerID.length() > 7) budget = Double.parseDouble(customerID.substring(7));

		switch(store) {
		case "ON": serverPort = 9000; break;
		case "BC": serverPort = 7000; break;
		case "QC": serverPort = 8000; break;
		default: serverPort = 8000;
		}
		// Create socket
		DatagramSocket mySocket = null;

		try {
			mySocket = new DatagramSocket();

			String dataToSend = customerID + "," + itemID + "," + type;

			// Byte array for the item ID
			byte[] item = dataToSend.getBytes();

			InetAddress host = InetAddress.getLocalHost();

			// Create packet
			DatagramPacket request = new DatagramPacket(item, dataToSend.length(), host, serverPort);

			// Send packet
			mySocket.send(request);
			System.out.println("Exchange request to " + store + " sent");

			byte[] buffer = new byte[1000]; // Byte array to receive response
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			mySocket.receive(reply);
			return new String(reply.getData()); // Receive status message from server

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mySocket != null) mySocket.close();
		}

		return "Operation was unsuccessful";
	}

	private String sendPurchaseRequest(String store, String customerID, String itemID, short day, short month, short year) {

		int serverPort = 0;

		switch(store) {
		case "ON": serverPort = 9000; break;
		case "BC": serverPort = 7000; break;
		case "QC": serverPort = 8000; break;
		default: serverPort = 8000;
		}
		// Create socket
		DatagramSocket mySocket = null;

		try {
			mySocket = new DatagramSocket();
			String dataToSend = customerID + "," + itemID + "," + day + "," + month + "," + year;

			// Byte array for the item ID
			byte[] item = dataToSend.getBytes();

			InetAddress host = InetAddress.getLocalHost();

			// Create packet
			DatagramPacket request = new DatagramPacket(item, dataToSend.length(), host, serverPort);

			// Send packet
			mySocket.send(request);
			System.out.println("Purchase request to " + store + " for " + itemID + " sent");

			byte[] buffer = new byte[1000]; // Byte array to receive response
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			mySocket.receive(reply);
			return new String(reply.getData()); // Receive status message from server

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mySocket != null) mySocket.close();
		}

		return "Operation was unsuccessful";
	}

	public static void setExchange(boolean b) {
		exchange = b;
	}

	private String sendFindItemRequest(int serverPort, String customerID, String itemName) {
		// Create socket
		DatagramSocket mySocket = null;

		try {
			mySocket = new DatagramSocket();

			String dataToSend = customerID + "," + itemName + ",f";

			// Byte array for the item ID
			byte[] item = dataToSend.getBytes();

			InetAddress host = InetAddress.getLocalHost();

			// Create packet
			DatagramPacket request = new DatagramPacket(item, dataToSend.length(), host, serverPort);

			// Send packet
			mySocket.send(request);
			System.out.println("Find request for " + itemName + " sent to " + serverPort);

			byte[] buffer = new byte[1000]; // Byte array to receive response
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			mySocket.receive(reply);
			return new String(reply.getData()); // Receive status message from server

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (mySocket != null) mySocket.close();
		}
		return "Operation was unsuccessful";
	}

	private String sendReturnRequest(String customerID, String itemID) {

		int serverPort = 0;

		// Get port for item's store
		switch(itemID.substring(0,2)) {
		case "ON": serverPort = 9000; break;
		case "BC": serverPort = 7000; break;
		case "QC": serverPort = 8000; break;
		default: serverPort = 8000;
		}

		// Create socket
		DatagramSocket mySocket = null;

		try {
			mySocket = new DatagramSocket();

			String dataToSend = customerID + "," + itemID + ",r";

			// Byte array for the item ID
			byte[] item = dataToSend.getBytes();

			InetAddress host = InetAddress.getLocalHost();

			// Create packet
			DatagramPacket request = new DatagramPacket(item, dataToSend.length(), host, serverPort);

			// Send packet
			mySocket.send(request);
			System.out.println("Return request for " + itemID + " sent to " + itemID.substring(0,2));

			byte[] buffer = new byte[1000]; // Byte array to receive response
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			mySocket.receive(reply);

			return new String(reply.getData()); // Receive status message from server

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (mySocket != null) mySocket.close();
		}
		return "Operation was unsuccessful";
	}

	private void receive() {

		DatagramSocket mySocket = null;
		try {
			mySocket = new DatagramSocket(port);
			byte[] buffer = new byte[1000];
			System.out.println("Server for port " + port + " ready");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				mySocket.receive(request);

				String rq = new String(request.getData()).trim();
				System.out.println("Message received: " + rq);

				byte[] serverResponse = new byte[1000];

				if(rq.matches("(ON|BC|QC)U[0-9]{4}([0-9.]+)*,(ON|BC|QC)[0-9]{4}(\\swait)*,[0-9]{1,2},[0-9]{1,2},[0-9]{4}")){ // Purchase item request

					String[] s = rq.split(",");

					a3.Date d = new a3.Date(Short.parseShort(s[2]), Short.parseShort(s[3]), Short.parseShort(s[4]));


					double a = purchaseItem(s[0], s[1], d);

					serverResponse = new String("" + a).getBytes();

				}
				else if(rq.matches("(ON|BC|QC)U[0-9]{4},(\\w\\s*)+,f")) { // Find item request
					String[] s = rq.split(",");

					for(Item a : inventory.values()) {
						if(a.itemName.equalsIgnoreCase(s[1])) {
							itemsFound.add(a.itemID + " " + a.quantity + " " + a.price + "\n");
						}
					}
					serverResponse = new String("").getBytes();

				}
				else if(rq.matches("(ON|BC|QC)U[0-9]{4}([0-9.]+)*,(ON|BC|QC)[0-9]{4},r")) { // Return item request
					String[] s = rq.split(",");


					double a = returnItem(s[0], s[1], a3.Date.getCurrentDate());
					serverResponse = new String("" + a).getBytes();

				}
				else if(rq.matches("(ON|BC|QC)U[0-9]{4}([0-9.]+)*,(ON|BC|QC)[0-9]{4},purchase")) { // Exchange item request

					// Perform checks for purchase
					boolean success = false;

					String[] s = rq.split(",");
					double budget = Double.parseDouble(s[0].substring(7));

					if(inventory.containsKey(s[1])) {
						Item i = inventory.get(s[1]);
						if(i.quantity > 0 && i.price < budget)
						{
							System.out.println("Purchase verification succeeded");
							success = true;
						} else System.out.println("Purchase verification failed");
					}

					serverResponse = (success)?new String("true").getBytes():new String("false").getBytes();

				}
				else if(rq.matches("(ON|BC|QC)U[0-9]{4},(ON|BC|QC)[0-9]{4},return")) { // Exchange item request

					// Perform checks for returns
					boolean success = false;
					String dateString = "";
					String[] s = rq.split(",");
					DSMSApp.Date dateOfReturn = DSMSApp.Date.getCurrentDate();

					if(inventory.containsKey(s[1])) {
						Item i = inventory.get(s[1]);

						if(i.IDOfBuyer.contains(s[0])){

							Calendar c = Calendar.getInstance();
							c.set(i.dateOfPurchase.getDay(), i.dateOfPurchase.getMonth(), i.dateOfPurchase.getYear());
							int dayOfYear = c.get(Calendar.DAY_OF_YEAR);

							c.set(dateOfReturn.Day, dateOfReturn.Month, dateOfReturn.Year);
							int dayOfYearReturn = c.get(Calendar.DAY_OF_YEAR);

							// If the item was purchased by this customer within the past 30 days, they can return it
							if((dayOfYearReturn - dayOfYear < 30) && (dayOfYearReturn - dayOfYear >= 0)){

								success = true;
								dateString = i.dateOfPurchase.getDay() + "-" + i.dateOfPurchase.getMonth() + "-" + i.dateOfPurchase.getYear();
								System.out.println("Return verification succeeded");
							}
							else System.out.println("Return verification failed");
						}
					}

					serverResponse = (success)?new String(dateString).getBytes():new String("false").getBytes();

				}

				DatagramPacket reply = new DatagramPacket(serverResponse, serverResponse.length, request.getAddress(), request.getPort());
				mySocket.send(reply);
				System.out.println("Reply sent!");
				buffer = new byte[1000];
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mySocket != null) mySocket.close();
		}
	}

}

