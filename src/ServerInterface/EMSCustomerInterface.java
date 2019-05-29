package ServerInterface;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface EMSCustomerInterface extends Remote{

	public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException;
	public HashMap<String, ArrayList<String>> getBookingSchedule(String customerID) throws RemoteException, NotBoundException;
	public int cancelEvent(String customerID, String eventID, String eventType) throws RemoteException;
}
