package server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import server.controller.rmiEMSInterfaceImplementation.EventDetails;

public class Events {

	// HashMap< EventType, HashMap< EventId, EventDetails >>
	public HashMap<String, ConcurrentHashMap<String, EventDetails>> eventsRecord = new HashMap<>();

	// HashMap< customerId, HashMap< dateString, ArrayList<eventIds> > >
	// dateString format "MMYY"
	public ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> customerBookingDetails = new ConcurrentHashMap<>();

}
