//package client;
//
//import client.customer.controller.CustomerClientManager;
//import server.controller.rmiEMSInterface.EMSEventManagerInterface;
//
//import java.rmi.ConnectException;
//import java.rmi.NotBoundException;
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//
//public class TestConcurrency implements Runnable {
//
//    String customerId;
//
//    public TestConcurrency(String customerId) {
//        this.customerId = customerId;
//    }
//
//    public static void main(String[] args) throws RemoteException, NotBoundException {
//
//        TestConcurrency testConcurrency1 = new TestConcurrency("MTLC0001");
//        Thread thread1 = new Thread(testConcurrency1);
//        thread1.start();
//
//        TestConcurrency testConcurrency2 = new TestConcurrency("MTLC0002");
//        Thread thread2 = new Thread(testConcurrency2);
//        thread2.start();
//    }
//
//    private static EMSEventManagerInterface getEMSInterfaceFromRegistry(Integer port) {
//        Registry registry = null;
//        EMSEventManagerInterface emsCustomerObj = null;
//        try {
//            registry = LocateRegistry.getRegistry(port);
//            System.out.println("\nConnection successfully established with the server\n");
//            emsCustomerObj = (EMSEventManagerInterface) registry.lookup("EMS");
//            System.out.println("\nRegistry lookup successful\n");
//        } catch (ConnectException e) {
//            System.err.println("Attempt to connect to a remote server failed!!");
//        } catch (NotBoundException e) {
//            System.err.println("Registry has no binding with the name 'EMS'.");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return emsCustomerObj;
//    }
//
//    @Override
//    public void run() {
//        try {
//            CustomerClientManager customerClientManager = new CustomerClientManager(this.customerId);
//            String acknowledgement = customerClientManager.bookEvent(this.customerId, "TORA100519", "Conference");
//            System.out.println(acknowledgement + this.customerId);
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (NotBoundException e) {
//            e.printStackTrace();
//        }
//    }
//    }
//}
//
