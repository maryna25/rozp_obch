import java.util.concurrent.locks.*;
import java.util.*;
import java.lang.InterruptedException;
import java.util.Arrays;

import static java.lang.Math.min;

public class BusGraph extends Thread {
    public static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    public static LinkedList<LinkedList<Integer>> matrix = new LinkedList<LinkedList<Integer>>();
    public static int n;
    public final int INF = Integer.MAX_VALUE;

    public BusGraph(int size){
        n = size;
        for (int i = 0; i < n; i++){
            matrix.add(new LinkedList<Integer>());
            for (int j = 0; j < n; j++){
                matrix.get(i).add(INF);
            }
        }
    }

    class AddPath extends Thread {
        private int n1, n2;
        public AddPath(){
        }

        public void run() {
            Random random = new Random();
            while(true){
                try {
                    rwl.writeLock().lock();
                    n1 = random.nextInt(n);
                    n2 = random.nextInt(n);
                    if (matrix.get(n1).get(n2) == INF) {
                        matrix.get(n1).set(n2, 1);
                        matrix.get(n2).set(n1, 1);
                        System.out.println("Add path " + n1 + " - " + n2);
                    } else {
                        matrix.get(n1).set(n2, INF);
                        matrix.get(n2).set(n1, INF);
                        System.out.println("Remove path " + n1 + " - " + n2);
                    }
                    rwl.writeLock().unlock();
                    sleep(5000);
                } catch (InterruptedException ex) { }

            }
        }
    }

    class ChangePrice extends Thread {
        private int n1, n2, value;
        public ChangePrice(){
        }

        public void run() {
            Random random = new Random();
            while(true){
                try {
                    rwl.writeLock().lock();
                    do {
                        n1 = random.nextInt(n);
                        n2 = random.nextInt(n);
                    } while (matrix.get(n1).get(n2) == INF);
                    value = random.nextInt(100);
                    matrix.get(n1).set(n2, value);
                    matrix.get(n2).set(n1, value);
                    rwl.writeLock().unlock();
                    System.out.println("Price for path " + n1 + " - " + n2 + " is " + value + " now");
                    sleep(7000);
                } catch (InterruptedException ex) { }

            }
        }
    }

    class AddCity extends Thread {
        private int n1;
        public AddCity(){
        }

        public void run() {
            Random random = new Random();
            while(true){
                try {
                    rwl.writeLock().lock();
                    n1 = random.nextInt(n * 2);
                    if (n1 >= n) {
                        for (int i = 0; i < n; i++) {
                            matrix.get(i).add(INF);
                        }
                        matrix.add(new LinkedList<Integer>());
                        n++;
                        for (int j = 0; j < n; j++) {
                            matrix.get(n - 1).add(INF);
                        }
                        System.out.println("Add new city!");
                    } else {
                        for (int i = 0; i < n; i++) {
                            matrix.get(i).remove(n1);
                        }
                        matrix.remove(n1);
                        n--;
                        System.out.println("Remove city " + n1);
                    }
                    rwl.writeLock().unlock();
                    sleep(5000);
                } catch (InterruptedException ex) { }

            }
        }
    }

    class FindPath extends Thread {
        private int n1, n2;
        public FindPath(){
        }

        public void run() {
            Random random = new Random();
            while(true){
                try {
                    n1 = random.nextInt(n);
                    do {
                        n2 = random.nextInt(n);
                    } while (n2 == n1);
                    rwl.readLock().lock();

                    boolean[] used = new boolean[n];
                    int[] dist = new int[n];
                    Arrays.fill(dist, INF);
                    dist[n1] = 0;
                    for (; ; ) {
                        int v = -1;
                        for (int nv = 0; nv < n; nv++)
                            if (!used[nv] && dist[nv] < INF && (v == -1 || dist[v] > dist[nv]))
                                v = nv;
                        if (v == -1) break;
                        used[v] = true;
                        for (int nv = 0; nv < n; nv++)
                            if (!used[nv] && matrix.get(v).get(nv) < INF)
                                dist[nv] = min(dist[nv], dist[v] + matrix.get(v).get(nv));
                        }
                    rwl.readLock().unlock();
                    if (dist[n2] < INF){
                        System.out.println("Found path " + n1 + " - " + n2 + ". Price is " + dist[n2]);
                    } else {
                        System.out.println("There is no path between " + n1 + " - " + n2);
                    }
                    sleep(7000);
                } catch (InterruptedException ex) { }

            }
        }
    }


    public static void main(String args[]) {
        BusGraph bg = new BusGraph(10);
        bg.start();
    }

    public void run() {
        AddPath ap = new AddPath();
        ap.start();

        ChangePrice cp = new ChangePrice();
        cp.start();

        AddCity ac = new AddCity();
        ac.start();

        FindPath fp = new FindPath();
        fp.start();

        while(true){
            try {
                sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
    }
}