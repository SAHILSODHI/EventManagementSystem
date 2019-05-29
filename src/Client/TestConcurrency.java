package Client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TestConcurrency implements Runnable{

    String customerId;
    public TestConcurrency(String customerId) {
        this.customerId = customerId;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException{

        TestConcurrency testConcurrency1 = new TestConcurrency("MTLC0001");
        Thread thread1 = new Thread(testConcurrency1);
        thread1.start();

        TestConcurrency testConcurrency2 = new TestConcurrency("MTLC0002");
        Thread thread2 = new Thread(testConcurrency2);
        thread2.start();

    }

    @Override
    public void run() {
        try {
            CustomerClientManager customerClientManager = new CustomerClientManager(this.customerId);
            String acknowledgement = customerClientManager.bookEvent(this.customerId, "TORA100519", "Conference");
            System.out.println(acknowledgement + this.customerId);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
