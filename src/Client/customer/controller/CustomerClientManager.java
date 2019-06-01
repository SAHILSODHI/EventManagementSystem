package client.customer.controller;

import serverInterface.EMSCustomerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerClientManager implements EMSCustomerInterface {

    private EMSCustomerInterface emsCustomerObj;

    public CustomerClientManager(EMSCustomerInterface emsCustomerObj) {

        super();
        this.emsCustomerObj = emsCustomerObj;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return emsCustomerObj.bookEvent(customerID, eventID, eventType);
    }

    @Override
    public HashMap<String, ArrayList<String>> getBookingSchedule(String customerID) throws RemoteException, NotBoundException {

        return emsCustomerObj.getBookingSchedule(customerID);
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return emsCustomerObj.cancelEvent(customerID, eventID, eventType);
    }
}
