package a3;

public class DSMSServer {
	public static void main(String args[]) {
		
		try{

			Runnable QCServer = () -> {
				System.out.println("QCServer Started...");
				DSMSImpl impl = new DSMSImpl("QC");
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/comp", impl);
			};

			Runnable ONServer = () -> { 
				System.out.println("ONServer Started...");
				DSMSImpl impl = new DSMSImpl("ON");
				impl.inventory.put("ON1000", new Item("ON1000", "Shirt", 20, 1000.01));
				impl.inventory.put("ON2000", new Item("ON2000", "Tank top", 10, 8.00));
				impl.inventory.put("ON3000", new Item("ON3000", "Socks", 5, 5.50));
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/comp", impl);
			};
			Runnable BCServer = () -> {
				System.out.println("BCServer Started...");
				DSMSImpl impl = new DSMSImpl("BC");
				//Endpoint endpoint = Endpoint.publish("http://localhost:8080/comp", impl);
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