package implementRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import serverInterface.EMSEventManagerInterface;

/**
 *	Resource ordering
 *	Data manipulation to be done in the following order
 *	1. events
 *  2. customerBookingDetails
 */

public class EMS extends UnicastRemoteObject implements EMSEventManagerInterface {

	// HashMap< EventType, HashMap< EventId, EventDetails >>
	HashMap<String, ConcurrentHashMap<String, EventDetails>> events = new HashMap<>();

	// HashMap< customerId, HashMap< dateString, ArrayList<eventIds> > >
	// dateString format "MMYY"
	public ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> customerBookingDetails = new ConcurrentHashMap<>();

	public EMS() throws RemoteException {
		super();
	}

	@Override
	public String addEvent(String eventID, String eventType, Integer bookingCapacity)
			throws RemoteException {

		String response = "";

		EventDetails newEventDetails = new EventDetails();
		newEventDetails.bookingCapacity = bookingCapacity;
		newEventDetails.remainingCapacity = bookingCapacity;
		newEventDetails.eventDate = eventID.substring(4);

		ConcurrentHashMap<String, EventDetails> upComingEvents = events.get(eventType);

		if (upComingEvents == null) {
			events.put(eventType, new ConcurrentHashMap<>());
			upComingEvents = events.get(eventType);
		}

		if (upComingEvents.containsKey(eventID)) {
			if (upComingEvents.get(eventID).bookingCapacity <= bookingCapacity) {

				response = "Event " + eventID + "  already exists. Booking capacity increased from "
						+ upComingEvents.get(eventID).bookingCapacity + " to " + bookingCapacity;
				upComingEvents.get(eventID).bookingCapacity = bookingCapacity;

			} else if ((upComingEvents.get(eventID).bookingCapacity - bookingCapacity) <= upComingEvents
					.get(eventID).remainingCapacity) {

				response = "Event " + eventID + "  already exists. Booking capacity decreased from "
						+ upComingEvents.get(eventID).bookingCapacity + " to " + bookingCapacity;

				upComingEvents.get(eventID).remainingCapacity = bookingCapacity
						- (upComingEvents.get(eventID).bookingCapacity - upComingEvents.get(eventID).remainingCapacity);
				upComingEvents.get(eventID).bookingCapacity = bookingCapacity;

			} else {
				response = "Operation add event failed for event " + eventID;
			}
		} else {
			upComingEvents.put(eventID, newEventDetails);
			response = "Event " + eventID + " added successfully";
		}

		System.out.println(response);

		return response;
	}

	@Override
	public String removeEvent(String eventID, String eventType) throws RemoteException {
		String response = "";

		ConcurrentHashMap<String, EventDetails> upComingEvents = events.get(eventType);

		if (upComingEvents == null) {
			response = "No events found for Event Type: " + eventType;
		} else {
			
			EventDetails eventDetails = upComingEvents.get(eventID);
			
			if(eventDetails == null) {
				response = "No events found with Event ID: " + eventID;
			} else {
				if(eventDetails.bookingCapacity != eventDetails.remainingCapacity) {
					cancelCustomerBookingsForDeletedEvent(eventID, eventDetails);
				}
				upComingEvents.remove(eventID);
				response = "Event " + eventID + " successfully deleted";
			}
		}

		System.out.println(response);
		
		return response;
	}

	private void cancelCustomerBookingsForDeletedEvent(String eventID, EventDetails eventDetails) {
		for(String customerID : eventDetails.bookedCustomerIDs) {
			ArrayList<String> bookedEvents = customerBookingDetails.get(customerID).get(eventDetails.eventDate.substring(2));
			bookedEvents.remove(eventID);
			System.out.println("Booking for event "+ eventID +"canceled for customer " + customerID);
		}
	}

	@Override
	public int listEventAvailability(String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {

		//check if events for the particular eventType and EventID
		if (this.events.get(eventType) == null || this.events.get(eventType).get(eventID) == null)
		{
			return "No events found";
		}

		Integer remainingCapacity = this.events.get(eventType).get(eventID).remainingCapacity;

		if (remainingCapacity > 0) {

			remainingCapacity -= 1;
			HashMap<String, ArrayList<String>> customerDetails = new HashMap<>();
			ArrayList<String> eventIds = new ArrayList<>();
			String bookingDate = eventID.substring(6, 10);
			String customerNativeCity = customerID.substring(0, 3);
			String eventCityToBeBookedIn = eventID.substring(0,3);
			int numberOfForeignCities = 0;

			// check if booking id already exists
			if (customerBookingDetails.containsKey(customerID)) {
				if (customerBookingDetails.get(customerID).containsKey(bookingDate)) {
					eventIds = customerBookingDetails.get(customerID).get(bookingDate);
					if (eventIds.contains(eventID)) {
						return "Booking for the event already exists";
					}
				}
			}

			// check if the number of bookings in non-native city is greater than 3
			for (String eventTypeKey : customerBookingDetails.keySet()) {
				if (customerBookingDetails.get(eventTypeKey).containsKey(bookingDate)) {

					ArrayList<String> events = customerBookingDetails.get(eventTypeKey).get(bookingDate);
					for (String ids : events) {
						if (!ids.startsWith(customerNativeCity)) {
							++numberOfForeignCities;
						}
					}
				}
			}

			if(!eventCityToBeBookedIn.equals(customerNativeCity)){
				++numberOfForeignCities;
			}

			if (numberOfForeignCities > 3) {
				return "You cannot add more than 3 events in cities out of " + customerNativeCity
						+ " for same month in a calender year";
			}

			eventIds.add(eventID);
			customerDetails.put(bookingDate, eventIds);
			this.customerBookingDetails.put(customerID, customerDetails);
			events.get(eventType).get(eventID).remainingCapacity = remainingCapacity;
			//TODO Add customer id to the hashset
			//events.get(eventType).get(eventID).bookedCustomerIDs.add(customerID);
			return "Booking is successful";
		} else
			return "Booking capacity is full";
	}

	public HashMap<String, ArrayList<String>> getBookingSchedule(String customerID)
			throws RemoteException, NotBoundException {

		if (this.customerBookingDetails.containsKey(customerID)) {
			return this.customerBookingDetails.get(customerID);
		} else
			return new HashMap<>();
	}

	public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {

		// HashMap< EventType, HashMap< EventId, EventDetails >>
		// HashMap<String, ConcurrentHashMap<String, EventDetails>> events = new HashMap<>();

		// HashMap< customerId, HashMap< dateString, ArrayList<eventIds> > >
		// dateString format "MMYY"
		// public ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> customerBookingDetails = new ConcurrentHashMap<>();

		if(this.events.isEmpty()){
			return "No such event found, events empty";
		}

		if(!this.events.containsKey(eventType)){
			return "No such event found with the given event type";
		}

		String dateString = eventID.substring(6, 10);

		if(!this.events.get(eventType).contains(eventID)){
			return "No such event found, no eventID match";
		}

		this.events.get(eventType).get(eventID).remainingCapacity += 1;
		// TODO remove customer id from eventDetails
		//events.get(eventType).get(eventID).bookedCustomerIDs.remove(customerID);

		this.customerBookingDetails.get(customerID).get(dateString).remove(eventID);

		return "Event successfully removed";
	}
}
