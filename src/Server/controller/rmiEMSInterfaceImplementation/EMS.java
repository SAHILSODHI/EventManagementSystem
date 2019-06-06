package server.controller.rmiEMSInterfaceImplementation;

import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import server.controller.interserver.InterServerUDPCommunication;
import server.controller.rmiEMSInterface.EMSEventManagerInterface;
import server.model.Events;

/**
 * Resource ordering Data manipulation to be done in the following order 1.
 * events 2. customerBookingDetails
 */

public class EMS extends UnicastRemoteObject implements EMSEventManagerInterface {

	private Events events;

	private InterServerUDPCommunication interServer;

	public EMS(String localServerPortName) throws RemoteException, SocketException {
		super();
		events = new Events();
		interServer = new InterServerUDPCommunication(localServerPortName, events);
	}

	@Override
	public String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException {

		String response = "";

		EventDetails newEventDetails = new EventDetails();
		newEventDetails.bookingCapacity = bookingCapacity;
		newEventDetails.remainingCapacity = bookingCapacity;
		newEventDetails.eventDate = eventID.substring(4);

		ConcurrentHashMap<String, EventDetails> upComingEvents = events.eventsRecord.get(eventType);

		if (upComingEvents == null) {
			events.eventsRecord.put(eventType, new ConcurrentHashMap<>());
			upComingEvents = events.eventsRecord.get(eventType);
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

		ConcurrentHashMap<String, EventDetails> upComingEvents = events.eventsRecord.get(eventType);

		if (upComingEvents == null) {
			response = "No events found for Event Type: " + eventType;
		} else {

			EventDetails eventDetails = upComingEvents.get(eventID);

			if (eventDetails == null) {
				response = "No events found with Event ID: " + eventID;
			} else {
				if (eventDetails.bookingCapacity != eventDetails.remainingCapacity) {
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
		for (String customerID : eventDetails.bookedCustomerIDs) {
			ArrayList<String> bookedEvents = events.customerBookingDetails.get(customerID)
					.get(eventDetails.eventDate.substring(2));
			bookedEvents.remove(eventID);
			System.out.println("Booking for event " + eventID + "canceled for customer " + customerID);
		}
	}

	@Override
	public HashMap<String, Integer> listEventAvailability(String eventType) throws RemoteException {
		return interServer.getEventAvailability(eventType);
	}

	public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
		return interServer.getBookEventStatus(customerID, eventID, eventType);
	}

	public ArrayList<String> getBookingSchedule(String customerID)
			throws RemoteException, NotBoundException {
		return interServer.getAllBookingSchedule(customerID);
	}

	public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {
		return interServer.cancelEvent(customerID, eventID, eventType);
	}
}