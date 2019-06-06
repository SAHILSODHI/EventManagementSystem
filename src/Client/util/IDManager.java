package client.util;

import java.util.Scanner;

public class IDManager {

	private static Scanner scanner = new Scanner(System.in);

	public static String getCustomerID() {
		return getID('C');
	}
	
	public static String getSpecificServersCustomerID(String serverName) {
		String customerID = "";
		do {
			customerID = getID('C');
			if (!customerID.substring(0,3).equalsIgnoreCase(serverName)) System.out.println("Customer does not belong to " + serverName + " server. Please Enter valid Customer ID.");
		} while (!customerID.substring(0,3).equalsIgnoreCase(serverName));
		
		return customerID;
	}

	public static String getManagerID() {
		return getID('M');
	}

	private static String getID(char clientType) {
		String id = "";

		do {
			System.out.print("Enter ID: ");
			id = scanner.nextLine();
		} while (!isIdValid(id, clientType));

		return id;
	}

	private static Boolean isIdValid(String id, char clientType) {

		Boolean result = true;
		String city = "";

		if (id.length() != 8) {
			result = false;
		} else {
			city = id.substring(0, 3);
		}

		if (!result
				|| (!city.equalsIgnoreCase("TOR") && !city.equalsIgnoreCase("MTL") && !city.equalsIgnoreCase("OTW"))
				|| id.charAt(3) != clientType 
				|| isNaN(id.substring(4))) {
			result = false;
		}

		if (!result)
			System.out.println("Invalid ID!! Please try again...");

		return result;
	}

	private static boolean isNaN(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return true;
		}
		return false;
	}

}
