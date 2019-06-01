package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import enums.PORTS;
import implementRemoteInterface.EMS;

public class OttawaServer {
	
	public static void main(String[] args) throws Exception
	{
		EMS obj = new EMS();
		
		Registry registry = LocateRegistry.createRegistry(PORTS.OTW.label);
		registry.rebind("EMS", obj);
		System.out.println("Ottawa server up and running!!!");
	}
	
}
