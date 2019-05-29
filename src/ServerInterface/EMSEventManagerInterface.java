package ServerInterface;

import java.rmi.*;

public interface EMSEventManagerInterface extends Remote, EMSCustomerInterface{

	public String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException;
	public int removeEvent(String eventID, String eventType) throws RemoteException;
	public int listEventAvailability(String eventType) throws RemoteException;
	
}
