package server.controller.rmiEMSInterface;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface EMSCustomerInterface extends Remote{

	String bookEvent(String customerID, String eventID, String eventType) throws RemoteException;
	ArrayList<String> getBookingSchedule(String customerID) throws RemoteException, NotBoundException;
	String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException;
}
