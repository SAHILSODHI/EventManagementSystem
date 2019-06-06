package client.eventmanager.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.eventmanager.controller.EventManagerClientManager;
import client.util.IDManager;

public class EventManagerClientView {

	String eventManagerID;
	String localServerName;
	EventManagerClientManager eventManagerClientManager;
	BufferedReader bufferedReader;

	public EventManagerClientView(String eventManagerID, EventManagerClientManager eventManagerClientManager) {
		this.eventManagerID = eventManagerID;
		this.localServerName = this.eventManagerID.substring(0, 3);
		this.eventManagerClientManager = eventManagerClientManager;
		this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	}

	public boolean validateChoice(String inputType, String input, Integer minInput, Integer maxInput) {

		if (inputType.equals("Number")
				&& (isNaN(input) || Integer.parseInt(input) < minInput || Integer.parseInt(input) > maxInput)) {
			System.out.println("Invalid input!!\n");
			return false;
		} else if (inputType.equals("Date")) {
			Pattern pattern = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\d\\d");
			Matcher matcher = pattern.matcher(input);
			if (input.length() != 10 || !matcher.find()) {
				System.out.println("Input Date doesn't match the required pattern");
				return false;
			}
			// TODO : validate if date is before the current date
		}
		return true;
	}

	private String getUserInputAndValidate(String menu, String inputType, Integer minChoice, Integer maxChoice)
			throws IOException {

		String input;

		do {
			System.out.println("\n" + menu);
			System.out.print("\nEnter input: ");
			input = bufferedReader.readLine().trim();
		} while (!validateChoice(inputType, input, minChoice, maxChoice));

		if (inputType.equals("Date")) {
			input = input.replaceAll("/", "");
			input = input.substring(0, 4) + input.substring(6);
		}

		return input;
	}

	private boolean isNaN(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return true;
		}
		return false;
	}

	public void renderView() throws IOException, NotBoundException {
		String userInput;

		while (true) {
			String eventID = "";
			String serverResponse = "";
			String customerId = "";
			String eventType = "";

			userInput = getUserInputAndValidate(
					"\nMain Menu\n1. Add Event\n2. Remove Event\n3. List Event Availability\n4. Book an event for a customer\n5. Get customer's bookings schedule\n6. Cancel an event for a customer",
					"Number", 1, 6);

			switch (userInput) {
			case "1":
				eventType = getEventTypeFromUser();
				eventID = getEventIDFromUser(true);
				Integer bookingCapacity = Integer
						.parseInt(getUserInputAndValidate("Booking Capacity: ", "Number", 1, Integer.MAX_VALUE));
				serverResponse = eventManagerClientManager.addEvent(eventID, eventType, bookingCapacity);
				System.out.println("server Response: " + serverResponse + "\n");
				break;

			case "2":
				eventType = getEventTypeFromUser();
				eventID = getEventIDFromUser(true);
				serverResponse = eventManagerClientManager.removeEvent(eventID, eventType);
				System.out.println("server Response: " + serverResponse + "\n");
				break;

			case "3":
				eventType = getEventTypeFromUser();
				System.out.println(eventManagerClientManager.listEventAvailability(eventType));
				break;
			case "4":
				customerId = IDManager.getSpecificServersCustomerID(localServerName);
				eventType = getEventTypeFromUser();
				eventID = getEventIDFromUser(false);
				serverResponse = eventManagerClientManager.bookEvent(customerId, eventID, eventType);
				System.out.println("server Response: " + serverResponse + "\n");
				break;
			case "5":
				customerId = IDManager.getSpecificServersCustomerID(localServerName);
				ArrayList<String> allEvents = eventManagerClientManager.getBookingSchedule(customerId);
				System.out.println("\nList of events booked: ");
				for(String key: allEvents){
					System.out.println(key);
				}
				System.out.println("\n");
				break;

			case "6":
				customerId = IDManager.getSpecificServersCustomerID(localServerName);
				eventType = getEventTypeFromUser();
				eventID = getEventIDFromUser(false);
				serverResponse = eventManagerClientManager.cancelEvent(customerId, eventID, eventType);
				System.out.println("server Response: " + serverResponse + "\n");
				break;
			}
		}
	}

	private String getEventTypeFromUser() throws IOException {
		String eventType = getUserInputAndValidate("Select Event type \n1. Conference\n2. Seminar \n3. Trade Show",
				"Number", 1, 3);
		eventType = eventType.equals("1") ? "Conference" : eventType.equals("2") ? "Seminar" : "Trade Show";
		return eventType;
	}

	private String getEventIDFromUser(Boolean isLocalEvent) throws IOException {

		String eventCity = "";

		if (!isLocalEvent) {
			eventCity = getUserInputAndValidate("Select Event City \n1. TOR\n2. MTL \n3. OTW", "Number", 1, 3);
			eventCity = eventCity.equals("1") ? "TOR" : eventCity.equals("2") ? "MTL" : "OTW";
		} else {
			eventCity = localServerName;
		}

		String eventID = null;

		String eventDate = getUserInputAndValidate("Event Date (DD/MM/YYYY)", "Date", null, null);
		String eventTimeSlot = getUserInputAndValidate("Event Slot\n1.Morning\n2.Afternoon\n3.Evening", "Number", 1, 3);
		eventTimeSlot = eventTimeSlot.equals("1") ? "M" : eventTimeSlot.equals("2") ? "A" : "E";

		eventID = eventCity + eventTimeSlot + eventDate;

		return eventID;
	}
}
