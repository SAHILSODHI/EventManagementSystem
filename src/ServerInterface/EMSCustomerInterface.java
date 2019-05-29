package ServerInterface;

import java.rmi.*;

public interface EMSCustomerInterface extends Remote{

	public int bookEvent(String customerID, String eventID, String eventType) throws RemoteException;
	public int getBookingSchedule(String customerID) throws RemoteException;
	public int cancelEvent(String customerID, String eventID) throws RemoteException;
}
