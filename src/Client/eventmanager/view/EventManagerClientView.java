package client.eventmanager.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.eventmanager.controller.EventManagerClientManager;

public class EventManagerClientView {

	String eventManagerID;
	String nativeCityCode;
	EventManagerClientManager eventManagerClientManager;
	BufferedReader bufferedReader;

	public EventManagerClientView(String eventManagerID, EventManagerClientManager eventManagerClientManager) {
		this.eventManagerID = eventManagerID;
		this.nativeCityCode = this.eventManagerID.substring(0, 3);
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
			if(input.length()!=10 || !matcher.find()) {
				System.out.println("Input Date doesn't match the required pattern");
				return false;
			}
			//TODO : validate if date is before the current date
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

		if(inputType.equals("Date")) {
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
			userInput = getUserInputAndValidate(
					"\nMain Menu\n1. Add Event\n2. Remove Event\n3. List Event Availability", "Number", 1, 3);

			String eventType = getUserInputAndValidate("Select Event type \n1. Conference\n2. Seminar \n3. Trade Show",
					"Number", 1, 3);
			eventType = eventType.equals("1") ? "Conference" : eventType.equals("2") ? "Seminar" : "Trade Show";

			switch (userInput) {
			case "1":
				String eventID = getEventIDFromUser();
				Integer bookingCapacity = Integer.parseInt(getUserInputAndValidate("Booking Capacity: ", "Number", 1, Integer.MAX_VALUE));
				String serverResponse = eventManagerClientManager.addEvent(eventID, eventType, bookingCapacity);
				System.out.println("server Response: " + serverResponse + "\n");
				break;

			case "2":
				eventID = getEventIDFromUser();
				serverResponse = eventManagerClientManager.removeEvent(eventID, eventType);
				System.out.println("server Response: " + serverResponse + "\n");
				break;

			case "3":
				eventManagerClientManager.listEventAvailability(eventType);
				break;
			}
		}
	}

	private String getEventIDFromUser() throws IOException {

		String eventID = null;

		String eventDate = getUserInputAndValidate("Event Date (DD/MM/YYYY)", "Date", null, null);
		String eventTimeSlot = getUserInputAndValidate("Event Slot\n1.Morning\n2.Afternoon\n3.Evening", "Number", 1, 3);
		eventTimeSlot = eventTimeSlot.equals("1") ? "M" : eventTimeSlot.equals("2") ? "A" : "E";

		eventID = this.nativeCityCode + eventTimeSlot + eventDate;

		return eventID;
	}
}
