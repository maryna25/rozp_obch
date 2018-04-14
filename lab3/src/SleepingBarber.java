import java.util.concurrent.Semaphore;

public class SleepingBarber extends Thread {

    public static Semaphore customers = new Semaphore(0);
    public static Semaphore barber = new Semaphore(0);
    public static Semaphore accessSeats = new Semaphore(1);

    public static final int CHAIRS = 4;
    public static int numberOfFreeSeats = CHAIRS;

    class Customer extends Thread {
        int id;
        boolean notCut = true;

        public Customer(int i) {
            id = i;
        }

        public void run() {
            while (notCut) {
                try {
                    accessSeats.acquire();
                    if (numberOfFreeSeats > 0) {
                        System.out.println("Customer " + this.id + " is waiting");
                        numberOfFreeSeats--;
                        customers.release(); //to wake up barber
                        accessSeats.release();
                        try {
                            barber.acquire();
                            notCut = false;
                            System.out.println("Customer " + this.id + " is getting his hair cut");
                            sleep(5100);
                        } catch (InterruptedException ex) {
                        }
                    } else { // there are no free seats
                        System.out.println("There are no free seats. Customer " + this.id + " has gone.");
                        accessSeats.release();
                        notCut = false;
                    }
                } catch (InterruptedException ex) {
                }
            }
        }

    }

    class Barber extends Thread {

        public Barber() {
        }

        public void run() {
            while (true) {
                try {
                    customers.acquire();
                    accessSeats.acquire(); // he has a job -> one customer stood up
                    numberOfFreeSeats++;
                    barber.release(); //to wake up customer
                    accessSeats.release();
                    System.out.println("The barber is cutting hair");
                    sleep(5000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public static void main(String args[]) {

        SleepingBarber barberShop = new SleepingBarber();
        barberShop.start();
    }

    public void run() {
        Barber b = new Barber();
        b.start();

        for (int i = 1; i < 11; i++) {
            Customer c = new Customer(i);
            c.start();
            try {
                sleep(2000);
            } catch (InterruptedException ex) {
            }
        }
    }
}