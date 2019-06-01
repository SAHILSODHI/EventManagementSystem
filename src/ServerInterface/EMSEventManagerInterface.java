package serverInterface;

import java.rmi.*;

public interface EMSEventManagerInterface extends Remote, EMSCustomerInterface{

	String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException;
	String removeEvent(String eventID, String eventType) throws RemoteException;
	int listEventAvailability(String eventType) throws RemoteException;
	
}
