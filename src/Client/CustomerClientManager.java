package Client;

import ServerInterface.EMSCustomerInterface;
import enums.PORTS;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerClientManager implements EMSCustomerInterface {

    private String server;
    private Integer port;
    private Registry registry;
    private EMSCustomerInterface obj;

    public CustomerClientManager(String customerId) throws RemoteException, NotBoundException {

        super();
        this.server = customerId.substring(0,3);
        this.port = PORTS.valueOf(this.server).label;
        this.registry = LocateRegistry.getRegistry(port);
        this.obj = (EMSCustomerInterface) registry.lookup("EMS");
    }

    @Override
    public synchronized String bookEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return obj.bookEvent(customerID, eventID, eventType);
    }

    @Override
    public synchronized HashMap<String, ArrayList<String>> getBookingSchedule(String customerID) throws RemoteException, NotBoundException {

        return obj.getBookingSchedule(customerID);
    }

    @Override
    public synchronized int cancelEvent(String customerID, String eventID, String eventType) throws RemoteException {

        return obj.cancelEvent(customerID, eventID, eventType);
    }
}
