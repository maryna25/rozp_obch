import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SemaphoreThreads extends BaseThreads{
    private JButton start1Button;
    private JButton start2Button;
    private JButton stop1Button;
    private JButton stop2Button;
    private JSlider slider;
    private JPanel rootPanel;

    private Thread thread1;
    private Thread thread2;
    private volatile int semaphore; // guarantees visibility of changes to variable across threads

    private SemaphoreThreads() {
        super();
        initialize();
        setUpView(rootPanel);
    }

    private void initialize() {
        initializeSlider(slider);

        //initializeButtons
        start1Button.setEnabled(true);
        start2Button.setEnabled(true);
        stop1Button.setEnabled(false);
        stop2Button.setEnabled(false);

        addListeners();
    }

    private void addListeners() {
        ActionListener listener = e -> {
            if (e.getSource() == start1Button) {
                if (semaphore != 0) {
                    System.out.println("Busy");
                    return;
                }
                semaphore = 1;

                thread1 = new Thread(new Task(10));
                thread1.setPriority(Thread.MIN_PRIORITY);
                thread1.start();

                start1Button.setEnabled(false);
                stop1Button.setEnabled(true);
            } else if (e.getSource() == stop1Button) {
                thread1.interrupt();
                start1Button.setEnabled(true);
                stop1Button.setEnabled(false);
                semaphore = 0;
            } else if (e.getSource() == start2Button) {
                if (semaphore != 0) {
                    System.out.println("Busy");
                    return;
                }
                semaphore = 1;

                thread2 = new Thread(new Task(90));
                thread2.setPriority(Thread.MAX_PRIORITY);
                thread2.start();

                start2Button.setEnabled(false);
                stop2Button.setEnabled(true);
            } else if (e.getSource() == stop2Button) {
                thread2.interrupt();
                start2Button.setEnabled(true);
                stop2Button.setEnabled(false);
                semaphore = 0;
            }
        };

        start1Button.addActionListener(listener);
        stop1Button.addActionListener(listener);

        start2Button.addActionListener(listener);
        stop2Button.addActionListener(listener);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (thread1 != null)
                    thread1.interrupt();
                if (thread2 != null)
                    thread2.interrupt();
                System.out.println("Bye");
                e.getWindow().dispose();
            }
        });
    }

    public static void main(String[] args) {
        new SemaphoreThreads();
    }
}
