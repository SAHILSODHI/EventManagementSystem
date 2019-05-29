package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CustomerClient {

	public static String getUserInput(String field) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter " + field + ": ");
		return br.readLine().trim();
	}

	private static boolean isNaN(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return true;
		}
		return false;
	}

	public static boolean validate(String customerId){

		String customerNativeCity = customerId.substring(0, 3);
		if((customerNativeCity.equalsIgnoreCase("TOR") || customerNativeCity.equalsIgnoreCase("MTL")
				|| customerNativeCity.equalsIgnoreCase("OTW")) && customerId.charAt(3) == 'C' &&
				(!isNaN(customerId.substring(4))))
			return true;
		else return false;
	}

	public static void showMenu(){
		System.out.println("Main Menu");
		System.out.println("1. Book an event");
		System.out.println("2. Get your bookings schedule");
		System.out.println("3. Cancel an event");
	}

	public static boolean validateCustomerChoice(int choice){
		if(choice > 0 && choice < 4)
			return true;
		else return false;
	}

	public static void main(String args[]) throws RemoteException, NotBoundException, MalformedURLException, IOException
	{
		boolean isValidCustomerId = false;
		boolean isValidChoice = false;
		String customerId = "";
		Integer choice = 0;

		while(true) {
			while (!isValidCustomerId) {
				customerId = getUserInput("Customer Id");
				isValidCustomerId = validate(customerId);
			}
			CustomerClientManager customerClientManager = new CustomerClientManager(customerId);
			while (true) {
				showMenu();
				while (!isValidChoice) {
					choice = Integer.parseInt(getUserInput("your choice").trim());
					isValidChoice = validateCustomerChoice(choice);
				}
				switch (choice) {
					case 1:
						String eventDate = getUserInput("Event Date").trim();
						String eventType = getUserInput("Event type \n1. Conference\n2. Seminar \n3. Trade Show");
						while(!(eventType.equals("Conference") || eventType.equals("Seminar") ||
								eventType.equals("Trade Show"))){
							eventType = getUserInput("correct event type \n1. Conference\n2. Seminar \n3. Trade Show");
						}
						String eventTimeSlot = getUserInput("Event Slot\n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
						while(!(eventTimeSlot.equals("M") || eventTimeSlot.equals("A") ||
								eventTimeSlot.equals("E"))){
							eventTimeSlot = getUserInput("correct event slot \n1.Morning(M)\n2.Afternoon(A)\n3.Evening(E)");
						}
						String cityToBookEvent = getUserInput("city to book event \n1. TOR\n2. MTL \n3. OTW");
						while(!(cityToBookEvent.equals("TOR") || cityToBookEvent.equals("MTL") ||
								cityToBookEvent.equals("OTW"))){
							eventType = getUserInput("city to book event \n1. TOR\n2. MTL \n3. OTW");
						}
						String eventID = cityToBookEvent + eventTimeSlot + eventDate;
						String acknowledgement = customerClientManager.bookEvent(customerId, eventID,eventType);
						System.out.println("\n"+customerId+" "+acknowledgement+"\n");
						break;

					case 2:
						customerClientManager.getBookingSchedule(customerId);
						break;

					case 3:
						eventDate = getUserInput("Event Date");
						eventType = getUserInput("Event type \n1. Conference\n2. Seminar \n3. Trade Show");
						while(!(eventType.equals("Conference") || eventType.equals("Seminar") ||
								eventType.equals("Trade Show"))){
							eventType = getUserInput("correct event type \n1. Conference\n2. Seminar \n3. Trade Show");
						}
						eventTimeSlot = getUserInput("Event Slot\n1.Morning(M)\n2.Afternoon(A)\3.Evening(E)");
						while(!(eventTimeSlot.equals("M") || eventTimeSlot.equals("A") ||
								eventTimeSlot.equals("E"))){
							eventTimeSlot = getUserInput("correct event slot \n1.Morning(M)\n2.Afternoon(A)\3.Evening(E)");
						}
						eventID = customerId.substring(0, 3) + eventTimeSlot + eventDate;
						customerClientManager.cancelEvent(customerId, eventID, eventType);
						break;
				}
				isValidChoice = false;
			}
		}
	}
}
