package client.eventmanager.controller;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import server.controller.rmiEMSInterface.EMSEventManagerInterface;

public class EventManagerClientManager implements EMSEventManagerInterface {

    private EMSEventManagerInterface emsEventManagerObj;

    public EventManagerClientManager(EMSEventManagerInterface emsCustomerObj) {

        super();
        this.emsEventManagerObj = emsCustomerObj;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return emsEventManagerObj.bookEvent(customerID, eventID, eventType);
    }

    @Override
    public ArrayList<String> getBookingSchedule(String customerID) throws RemoteException, NotBoundException {

        return emsEventManagerObj.getBookingSchedule(customerID);
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return emsEventManagerObj.cancelEvent(customerID, eventID, eventType);
    }

	@Override
	public String addEvent(String eventID, String eventType, Integer bookingCapacity) throws RemoteException {

		return emsEventManagerObj.addEvent(eventID, eventType, bookingCapacity);
	}

	@Override
	public String removeEvent(String eventID, String eventType) throws RemoteException {

		return emsEventManagerObj.removeEvent(eventID, eventType);
	}

	@Override
	public HashMap<String, Integer> listEventAvailability(String eventType) throws RemoteException {

		return emsEventManagerObj.listEventAvailability(eventType);
	}
}
