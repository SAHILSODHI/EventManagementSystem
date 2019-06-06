package client.eventmanager.main;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import client.eventmanager.controller.EventManagerClientManager;
import client.eventmanager.view.EventManagerClientView;
import client.util.IDManager;
import enums.PORTS;
import server.controller.rmiEMSInterface.EMSEventManagerInterface;

public class EventManagerClient {

	public static void main(String[] args) throws Exception {

		System.out.println("################## Event Manager Client Application ##################");

		String eventManagerID = "";
		eventManagerID = IDManager.getManagerID();
		String server = eventManagerID.substring(0, 3);
		Integer port = PORTS.valueOf(server).label;

		EMSEventManagerInterface emsCustomerObj = getEMSInterfaceFromRegistry(port);

		if (emsCustomerObj != null) {
			System.out.println("################## Welcome to Event Manager Client Portal ##################");
			EventManagerClientManager eventManagerClientManager = new EventManagerClientManager(emsCustomerObj);
			EventManagerClientView eventManagerClientView = new EventManagerClientView(eventManagerID,
					eventManagerClientManager);
			eventManagerClientView.renderView();

		}
		System.out.println("################## Event Manager Client Application Terminated ##################");
	}

	private static EMSEventManagerInterface getEMSInterfaceFromRegistry(Integer port) {
		Registry registry = null;
		EMSEventManagerInterface emsCustomerObj = null;
		try {
			registry = LocateRegistry.getRegistry(port);
			System.out.println("\nConnection successfully established with the server\n");
			emsCustomerObj = (EMSEventManagerInterface) registry.lookup("EMS");
			System.out.println("\nRegistry lookup successful\n");
		} catch (ConnectException e) {
			System.err.println("Attempt to connect to a remote server failed!!");
		} catch (NotBoundException e) {
			System.err.println("Registry has no binding with the name 'EMS'.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return emsCustomerObj;
	}
}
