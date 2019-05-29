package ImplementRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ServerInterface.EMSEventManagerInterface;

public class EMS extends UnicastRemoteObject implements EMSEventManagerInterface{

	HashMap<String, HashMap<String, EventDetails>> event = new HashMap<>();
	
	//	HashMap< customerId, HashMap< dateString, ArrayList<eventIds> > >
	public HashMap<String, HashMap<String, ArrayList<String>>> customerBookingDetails = new HashMap<>();

	
	public EMS() throws Exception{
		super();
	}

	@Override
	public synchronized String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized int removeEvent(String eventID, String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int listEventAvailability(String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int getBookingSchedule(String customerID) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int cancelEvent(String customerID, String eventID) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
