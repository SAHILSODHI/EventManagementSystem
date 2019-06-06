package client.customer.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;

import client.customer.controller.CustomerClientManager;

public class CustomerClientView {
	
	String customerID;
	CustomerClientManager customerClientManager;
	
	public CustomerClientView(String customerId, CustomerClientManager customerClientManager) {
		this.customerID = customerId;
		this.customerClientManager = customerClientManager;
	}

	public static String getUserInput(String field) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter " + field + ": ");
		return br.readLine().trim();
	}

	public static void showMenu() {
		System.out.println("Main Menu");
		System.out.println("1. Book an event");
		System.out.println("2. Get your bookings schedule");
		System.out.println("3. Cancel an event");
	}

	public static boolean validateCustomerChoice(int choice) {
        return choice > 0 && choice < 4;
	}

	public void renderView() throws IOException, NotBoundException {
		boolean isValidChoice = false;
		String customerId = this.customerID;
		Integer choice = 0;

		while (true) {
			showMenu();
			while (!isValidChoice) {
				choice = Integer.parseInt(getUserInput("your choice").trim());
				isValidChoice = validateCustomerChoice(choice);
			}
			switch (choice) {
			case 1:
				String eventDate = getUserInput("Event Date(DDMMYY)");
				while (eventDate.length() != 6){
					eventDate = getUserInput("Event Date in format(DDMMYY)");
				}
				String eventType = getUserInput("Event type \n1. Conference\n2. Seminar \n3. Trade Show");
				while (!(eventType.equals("Conference") || eventType.equals("Seminar")
						|| eventType.equals("Trade Show"))) {
					eventType = getUserInput("correct event type \n1. Conference\n2. Seminar \n3. Trade Show");
				}
				String eventTimeSlot = getUserInput("Event Slot\n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
				while (!(eventTimeSlot.equals("M") || eventTimeSlot.equals("A") || eventTimeSlot.equals("E"))) {
					eventTimeSlot = getUserInput("correct event slot \n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
				}
				String cityToBookEvent = getUserInput("city to book event \n1. TOR\n2. MTL \n3. OTW");
				while (!(cityToBookEvent.equals("TOR") || cityToBookEvent.equals("MTL")
						|| cityToBookEvent.equals("OTW"))) {
					cityToBookEvent = getUserInput("city to book event \n1. TOR\n2. MTL \n3. OTW");
				}
				String eventID = cityToBookEvent + eventTimeSlot + eventDate;
				String acknowledgement = customerClientManager.bookEvent(customerId, eventID, eventType);
				System.out.println("\n" + customerId + " " + acknowledgement + "\n");
				break;

			case 2:
				ArrayList<String> allEvents = customerClientManager.getBookingSchedule(customerId);
				System.out.println("\nList of events booked: ");
				for(String key: allEvents){
					System.out.println(key);
				}
				System.out.println("\n");
				break;

			case 3:
				eventDate = getUserInput("date of booking(DDMMYY)");
				while(eventDate.length() != 6){
					eventDate = getUserInput("date of booking in format(DDMMYY)");
				}
				eventType = getUserInput("Event type \n1. Conference\n2. Seminar \n3. Trade Show");
				while (!(eventType.equals("Conference") || eventType.equals("Seminar")
						|| eventType.equals("Trade Show"))) {
					eventType = getUserInput("correct event type \n1. Conference\n2. Seminar \n3. Trade Show");
				}
				eventTimeSlot = getUserInput("Event Slot\n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
				while (!(eventTimeSlot.equals("M") || eventTimeSlot.equals("A") || eventTimeSlot.equals("E"))) {
					eventTimeSlot = getUserInput("correct event slot \n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
				}
				String cityWhereEventBooked = getUserInput("city where the event was booked \n1. TOR\n2. MTL \n3. OTW");
				while (!(cityWhereEventBooked.equals("TOR") || cityWhereEventBooked.equals("MTL")
						|| cityWhereEventBooked.equals("OTW"))) {
					cityWhereEventBooked = getUserInput("city where the event was booked \n1. TOR\n2. MTL \n3. OTW");
				}
				eventID = cityWhereEventBooked + eventTimeSlot + eventDate;
				System.out.println(customerClientManager.cancelEvent(customerId, eventID, eventType));
				break;
			}
			isValidChoice = false;
		}
	}
}
