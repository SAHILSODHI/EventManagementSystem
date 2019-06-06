package server.controller.interserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import enums.PORTS;
import enums.UDP_PORTS;
import server.controller.rmiEMSInterfaceImplementation.EventDetails;
import server.model.Events;

public class InterServerUDPCommunication {

	String localServerPortName;
	Integer currentServerPort;
	Events events = null;

	public InterServerUDPCommunication(String localServerPortName, Events events) throws SocketException {
		this.localServerPortName = localServerPortName;
		this.currentServerPort = PORTS.valueOf(localServerPortName).label;
		this.events = events;

		Runnable task = () -> {
			receiveRequestAndReply();
		};

		Thread thread = new Thread(task);
		thread.start();

	}

	public HashMap<String, Integer> getEventAvailability(String eventType) {

		HashMap<String, Integer> eventAvailability = new HashMap<>();

		for (PORTS port : PORTS.values()) {
			eventAvailability.putAll(getEventAvailability(eventType, port.name()));
		}

		return eventAvailability;
	}

	private HashMap<String, Integer> getEventAvailability(String eventType, String portName) {

		HashMap<String, Integer> eventDetails = new HashMap<>();

		if (portName.equals(localServerPortName)) {
			ConcurrentHashMap<String, EventDetails> upcomingEvents = events.eventsRecord.get(eventType);
			if(upcomingEvents != null) {
				for(String eventID : upcomingEvents.keySet()) {
					eventDetails.put(eventID, upcomingEvents.get(eventID).remainingCapacity);
				}
			}
			return eventDetails;
		}

		HashMap<String, String> requestToServer = new HashMap<>();
		requestToServer.put("requestType", "listEventAvailability");
		requestToServer.put("eventType", eventType);
		eventDetails = (HashMap<String, Integer>) sendRequestAndReceiveResponse(requestToServer, portName);

		return eventDetails;
	}

	public ArrayList<String> getAllBookingSchedule(String customerID) {

		ArrayList<String> allBookingSchedule = new ArrayList<>();

		for (PORTS port : PORTS.values()) {
			allBookingSchedule.addAll(getAllBookingSchedule(customerID,port.name()));
		}
		return allBookingSchedule;
	}

	private ArrayList<String> getAllBookingSchedule(String customerID, String portName) {

		ArrayList<String> eventIdsList = new ArrayList<>();

		if (portName.equals(localServerPortName)) {
			if(events.customerBookingDetails.containsKey(customerID)){
				for(String key: events.customerBookingDetails.get(customerID).keySet()){
					eventIdsList.addAll(events.customerBookingDetails.get(customerID).get(key));
				}
			}
			return eventIdsList;
		}

		HashMap<String, String> requestToServer = new HashMap<>();
		requestToServer.put("requestType", "getBookingSchedule");
		requestToServer.put("customerID", customerID);
		eventIdsList = (ArrayList<String>) sendRequestAndReceiveResponse(requestToServer, portName);

		return eventIdsList;
	}

	public String getBookEventStatus(String customerID, String eventID, String eventType) {

		int numberOfForeignCities = 0;
		for (PORTS port : PORTS.values()) {
			if(!port.name().equals(customerID.substring(0,3))) {
				numberOfForeignCities += getForeignCityCount(customerID, eventID, eventType, port.name());
			}
		}
		if(numberOfForeignCities < 3){
			String response = getBookEventStatus(customerID,eventID,eventType, eventID.substring(0,3));
			return response;
		}
		else return "Event could not be booked. Number of events booked in foreign cities has exceeded the limit(3).";
	}

	private Integer getForeignCityCount(String customerID, String eventID, String eventType, String portName){
		if (portName.equals(localServerPortName)) {
			if (events.customerBookingDetails.containsKey(customerID)) {
				HashMap<String, ArrayList<String>> customerEvents = events.customerBookingDetails.get(customerID);
				if(customerEvents.containsKey(eventID.substring(6,10))){
					return customerEvents.get(eventID.substring(6,10)).size();
				}
			}
			return 0;
		}
		HashMap<String, String> requestToServer = new HashMap<>();
		requestToServer.put("requestType", "foreignCityCount");
		requestToServer.put("customerID", customerID);
		requestToServer.put("eventID", eventID);
		requestToServer.put("eventType", eventType);
		return (Integer) sendRequestAndReceiveResponse(requestToServer, portName);
	}

	private String getBookEventStatus(String customerID, String eventID, String eventType, String portName) {

		if (portName.equals(localServerPortName)) {
			if (events.eventsRecord.get(eventType) == null
					|| events.eventsRecord.get(eventType).get(eventID) == null) {
				return "No event records found";
			}

			Integer remainingCapacity = events.eventsRecord.get(eventType).get(eventID).remainingCapacity;

			if (remainingCapacity > 0) {

				remainingCapacity -= 1;
				HashMap<String, ArrayList<String>> customerDetails = new HashMap<>();
				ArrayList<String> eventIds = new ArrayList<>();
				String bookingDate = eventID.substring(6, 10);

				// check if booking id already exists
				if (events.customerBookingDetails.containsKey(customerID)) {
					if (events.customerBookingDetails.get(customerID).containsKey(bookingDate)) {
						eventIds = events.customerBookingDetails.get(customerID).get(bookingDate);
						if (eventIds.contains(eventID)) {
							return "Event already exists";
						}
					}
				}

				eventIds.add(eventID);
				customerDetails.put(bookingDate, eventIds);
				events.customerBookingDetails.put(customerID, customerDetails);
				events.eventsRecord.get(eventType).get(eventID).remainingCapacity = remainingCapacity;
				events.eventsRecord.get(eventType).get(eventID).bookedCustomerIDs.add(customerID);
				return "Event has been booked";
			} else
				return "Event is all booked";
		}

		HashMap<String, String> requestToServer = new HashMap<>();
		requestToServer.put("requestType", "bookEvent");
		requestToServer.put("customerID", customerID);
		requestToServer.put("eventID", eventID);
		requestToServer.put("eventType", eventType);
		return (String) sendRequestAndReceiveResponse(requestToServer, portName);
	}

	public String cancelEvent(String customerID, String eventID, String eventType) {

		String portName = eventID.substring(0,3);
		return cancelEvent(customerID,eventID,eventType,portName);
	}

	private String cancelEvent(String customerID, String eventID, String eventType, String portName) {

		if (portName.equals(localServerPortName)) {
			if (events.eventsRecord.isEmpty()) {
				return "No events found";
			}

			if (!events.eventsRecord.containsKey(eventType)) {
				return "Event type not found";
			}

			String dateString = eventID.substring(6, 10);

			if (!events.eventsRecord.get(eventType).containsKey(eventID)) {
				return "Event Id not found";
			}

			events.eventsRecord.get(eventType).get(eventID).remainingCapacity += 1;
			events.eventsRecord.get(eventType).get(eventID).bookedCustomerIDs.remove(customerID);

			events.customerBookingDetails.get(customerID).get(dateString).remove(eventID);
			return "Event has been booked successfully";
		}

		HashMap<String, String> requestToServer = new HashMap<>();
		requestToServer.put("requestType", "cancelEvent");
		requestToServer.put("customerID", customerID);
		requestToServer.put("eventID", eventID);
		requestToServer.put("eventType", eventType);

		return (String) sendRequestAndReceiveResponse(requestToServer, portName);
	}

	private Object sendRequestAndReceiveResponse(HashMap<String, String> requestToServer, String portName) {

		DatagramSocket socket = null;
		Object response = null;

		try {

			socket = new DatagramSocket();

			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteOutputStream));
			os.flush();
			os.writeObject(requestToServer);
			os.flush();

			byte[] sendBuf = byteOutputStream.toByteArray();

			InetAddress host = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(sendBuf, sendBuf.length, host, UDP_PORTS.valueOf(portName).label);
			socket.send(request);

			byte[] responseBuffer = new byte[5000];
			DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
			socket.receive(responsePacket);

			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(responseBuffer);
			ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteInputStream));
			response = inputStream.readObject();
			inputStream.close();

		} catch (SocketException e) {
			System.out.println("Socket llll: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO  llllll: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
		return response;
	}

	private void receiveRequestAndReply() {

		DatagramSocket socket = null;

		try {

			socket = new DatagramSocket(UDP_PORTS.valueOf(localServerPortName).label);

			byte[] buffer = new byte[5000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);

				ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
				ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteStream));
				HashMap<String, String> requestType = (HashMap<String, String>) inputStream.readObject();
				inputStream.close();

				Object replyObject = new Object();

				if (requestType.containsKey("requestType")) {
					if (requestType.get("requestType").equalsIgnoreCase("listEventAvailability")) {
						String eventType = requestType.get("eventType");
						replyObject = getEventAvailability(eventType, localServerPortName);

					} else if (requestType.get("requestType").equalsIgnoreCase("bookEvent")) {
						String eventType = requestType.get("eventType");
						String customerID = requestType.get("customerID");
						String eventID = requestType.get("eventID");
						replyObject = getBookEventStatus(customerID, eventID, eventType, localServerPortName);

					} else if (requestType.get("requestType").equalsIgnoreCase("cancelEvent")) {
						String eventType = requestType.get("eventType");
						String customerID = requestType.get("customerID");
						String eventID = requestType.get("eventID");
						replyObject = cancelEvent(customerID, eventID, eventType, localServerPortName);

					} else if (requestType.get("requestType").equalsIgnoreCase("getBookingSchedule")) {
						String customerID = requestType.get("customerID");
						replyObject = getAllBookingSchedule(customerID, localServerPortName);
					} else if (requestType.get("requestType").equalsIgnoreCase("foreignCityCount")) {
						String eventType = requestType.get("eventType");
						String customerID = requestType.get("customerID");
						String eventID = requestType.get("eventID");
						replyObject = getForeignCityCount(customerID, eventID, eventType, localServerPortName);
					}
				}

				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteOutputStream));
				os.flush();
				os.writeObject(replyObject);
				os.flush();

				byte[] sendBuf = byteOutputStream.toByteArray();
				DatagramPacket replyPacket = new DatagramPacket(sendBuf, sendBuf.length, request.getAddress(),
						request.getPort());
				socket.send(replyPacket);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}

	}

}
