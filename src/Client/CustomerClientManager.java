package Client;

import ServerInterface.EMSCustomerInterface;
import enums.PORTS;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CustomerClientManager implements EMSCustomerInterface {

    private String customerId;
    private String server;
    private Integer port;
    private Registry registry;
    private EMSCustomerInterface obj;

    public CustomerClientManager(String customerId) throws Exception{

        this.customerId = customerId;
        this.server = customerId.substring(0,3);
        this.port = PORTS.valueOf(this.server).label;
        this.registry = LocateRegistry.getRegistry(port);
        this.obj = (EMSCustomerInterface) registry.lookup("EMS");
    }

    @Override
    public synchronized int bookEvent(String customerID, String eventID, String eventType) throws RemoteException {
        obj.bookEvent(customerID, eventID, eventType);
        return 0;
    }

    @Override
    public synchronized int getBookingSchedule(String customerID) throws RemoteException {
        obj.getBookingSchedule(customerID);
        return 0;
    }

    @Override
    public synchronized int cancelEvent(String customerID, String eventID) throws RemoteException {
        obj.cancelEvent(customerID, eventID);
        return 0;
    }
}
