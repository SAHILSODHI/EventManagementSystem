package Client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ServerInterface.EMSEventManagerInterface;
import enums.PORTS;

public class EventManagerClient {

	public static void main(String args[]) throws Exception {

		System.out.println("Welcome to Event Manager Portal!!\n");

		String id = IDManager.getManagerID();
		String city = id.substring(0, 3);

		Integer port = PORTS.valueOf(city).label;

		Registry registry = LocateRegistry.getRegistry(port);
		EMSEventManagerInterface obj = (EMSEventManagerInterface) registry.lookup("EMS");
		int n = obj.bookEvent("", "", "");
		System.out.println("Addition is : " + n);
	}
}
