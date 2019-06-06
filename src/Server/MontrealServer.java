package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import enums.PORTS;
import server.controller.rmiEMSInterfaceImplementation.EMS;

public class MontrealServer {

	public static void main(String[] args) throws Exception {
		
		EMS obj = new EMS(PORTS.MTL.name());
		Registry registry = LocateRegistry.createRegistry(PORTS.MTL.label);
		registry.rebind("EMS", obj);
		System.out.println("Montreal server up and running!!!");
	}
}
