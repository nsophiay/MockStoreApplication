package a3;

import javax.xml.ws.Endpoint;

public class DSMSServer {
	public static void main(String args[]) {
		
		try{

			Runnable QCServer = () -> {
				System.out.println("QCServer Started...");
				DSMSImpl impl = new DSMSImpl("QC");
				impl.inventory.put("QC1000", new Item("QC1000", "Das Kapital", 0, 25.00));
				impl.inventory.put("QC2000", new Item("QC2000", "Avengers Infinity War", 10, 20.00));
				impl.inventory.put("QC3000", new Item("QC3000", "Cosmos", 2, 17.50));
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/a3/WebInterface", impl);
			};

			Runnable ONServer = () -> { 
				System.out.println("ONServer Started...");
				DSMSImpl impl = new DSMSImpl("ON");
				impl.inventory.put("ON1000", new Item("ON1000", "Shirt", 20, 1000.01));
				impl.inventory.put("ON2000", new Item("ON2000", "Tank top", 10, 8.00));
				impl.inventory.put("ON3000", new Item("ON3000", "Socks", 5, 5.50));
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/a3/WebInterface", impl);
			};
			Runnable BCServer = () -> {
				System.out.println("BCServer Started...");
				DSMSImpl impl = new DSMSImpl("BC");
				impl.inventory.put("BC1000", new Item("BC1000", "Barbell", 0, 100.00));
				impl.inventory.put("BC2000", new Item("BC2000", "Dumbbells", 2, 34.00));
				impl.inventory.put("BC3000", new Item("BC3000", "Kettlebells", 1, 50.00));
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/a3/WebInterface", impl);
			};

			Thread thread = new Thread(QCServer);
			Thread thread2 = new Thread(ONServer);
			Thread thread3 = new Thread(BCServer);


			thread.start(); 
			thread2.start();
			thread3.start();


		}
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

	}
}