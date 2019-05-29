package Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ImplementRemoteInterface.EMS;
import enums.PORTS;

public class MontrealServer {
	
	public static void main(String args[]) throws Exception
	{
		EMS obj = new EMS();

		Registry registry = LocateRegistry.createRegistry(PORTS.MTL.label);
		registry.rebind("EMS", obj);
		System.out.println("Montreal Server up and running!!!");
	}
}
