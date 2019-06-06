package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import enums.PORTS;
import server.controller.rmiEMSInterfaceImplementation.EMS;

public class TorontoServer {

	public static void main(String[] args) throws Exception {

		EMS obj = new EMS(PORTS.TOR.name());

		Registry registry = LocateRegistry.createRegistry(PORTS.TOR.label);
		registry.rebind("EMS", obj);
		System.out.println("Toronto server up and running!!!");
	}

}
