package ImplementRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import ServerInterface.EMSEventManagerInterface;

public class EMS extends UnicastRemoteObject implements EMSEventManagerInterface{

	// HashMap< EventType, HashMap< EventId, EventDetails >>
	HashMap<String, HashMap<String, EventDetails>> event = new HashMap<>();

	// HashMap< customerId, HashMap< dateString, ArrayList<eventIds> > >
	// dateString format "MMYY"
	public HashMap<String, HashMap<String, ArrayList<String>>> customerBookingDetails = new HashMap<>();

	public EMS() throws RemoteException{
		super();
	}

	private synchronized String updateCustomerBookingDetails(String customerId, String eventID, String type){

		HashMap<String, ArrayList<String>> customerDetails = new HashMap<>();
		ArrayList<String> eventIds = new ArrayList<>();
		String bookingDate = eventID.substring(6, 10);
		String customerNativeCity = customerId.substring(0,3);

		if(type.equals("Add")){
			int numberOfForeignCities = 0;

			//check if booking id already exists
			if(customerBookingDetails.containsKey(customerId)){
				if(customerBookingDetails.get(customerId).containsKey(bookingDate)){
					eventIds = customerBookingDetails.get(customerId).get(bookingDate);
					if(eventIds.contains(eventID)){
						return "Booking for the event already exists";
					}
				}
			}

			//check if the number of bookings in non-native city is greater than 3
			for(String eventTypeKey: customerBookingDetails.keySet()){
				if(customerBookingDetails.get(eventTypeKey).containsKey(bookingDate)) {

					ArrayList<String> events = customerBookingDetails.get(eventTypeKey).get(bookingDate);
					for (String ids : events) {
						if (!ids.startsWith(customerNativeCity)) {
							++numberOfForeignCities;
						}
						if (numberOfForeignCities >= 3) {
							return "You cannot add more than 3 events in cities out of " + customerNativeCity + " for same month in a year";
						}
					}
				}
			}

			eventIds.add(eventID);
			customerDetails.put(bookingDate, eventIds);
			this.customerBookingDetails.put(customerId, customerDetails);
			return "Event has been added.";
		}
		return null;
	}

	private synchronized void updateEventDetails(Integer updateRemainingCapacity, String eventType, String eventID){
		event.get(eventType).get(eventID).remainingCapacity = updateRemainingCapacity;
	}

	@Override
	public synchronized String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException {

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
	public synchronized String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {

		//this.addEvent(eventID,eventType, 100);
		if(this.event.isEmpty())
			return "No events found";
		Integer remainingCapacity = this.event.get(eventType).get(eventID).remainingCapacity;
		if(remainingCapacity > 0){
			remainingCapacity -= 1;
			String acknowledgement = this.updateCustomerBookingDetails(customerID, eventID, "Add");
			if(acknowledgement.equals("Event has been added.")){
				this.updateEventDetails(remainingCapacity, eventType, eventID);
			}
			return acknowledgement;
		}
		else return "Booking capacity is full";
	}

	@Override
	public synchronized HashMap<String, ArrayList<String>> getBookingSchedule(String customerID) throws RemoteException, NotBoundException {


		if(this.customerBookingDetails.containsKey(customerID)){
			return this.customerBookingDetails.get(customerID);
		} else return new HashMap<>();
	}

	@Override
	public synchronized int cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
}
