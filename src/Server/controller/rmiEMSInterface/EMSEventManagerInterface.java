package server.controller.rmiEMSInterface;

import java.rmi.*;
import java.util.HashMap;

public interface EMSEventManagerInterface extends Remote, EMSCustomerInterface{

	String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException;
	String removeEvent(String eventID, String eventType) throws RemoteException;
	HashMap<String, Integer> listEventAvailability(String eventType) throws RemoteException;
	
}
