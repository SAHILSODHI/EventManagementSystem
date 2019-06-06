package server.controller.rmiEMSInterfaceImplementation;

import java.util.HashSet;

public class EventDetails {

	public Integer bookingCapacity;
	public Integer remainingCapacity;
	public String eventDate; /* DDMMYY */
	public HashSet<String> bookedCustomerIDs = new HashSet<>();
	
}
