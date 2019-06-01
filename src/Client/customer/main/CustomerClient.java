package client.customer.main;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import client.customer.controller.CustomerClientManager;
import client.customer.view.CustomerClientView;
import client.util.IDManager;
import enums.PORTS;
import serverInterface.EMSCustomerInterface;
import serverInterface.EMSEventManagerInterface;

public class CustomerClient {

	public static void main(String[] args) {

		System.out.println("################## Customer Client Application ##################");
		
		String customerId = "";
		customerId = IDManager.getCustomerID();
		String server = customerId.substring(0,3);
        Integer port = PORTS.valueOf(server).label;
        
        EMSCustomerInterface emsCustomerObj = getEMSInterfaceFromRegistry(port);
        
        if(emsCustomerObj != null) {
        	System.out.println("################## Welcome to Customer Client Portal ##################");
        	CustomerClientManager customerClientManager = new CustomerClientManager(emsCustomerObj);
            CustomerClientView customerClientView = new CustomerClientView(customerId, customerClientManager);
            try{
				customerClientView.renderView();
			} catch (IOException e){
            	e.printStackTrace();
			} catch (NotBoundException e){
            	e.printStackTrace();
			}
        }
        System.out.println("################## Customer Client Application Terminated ##################");
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
