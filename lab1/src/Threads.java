import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.*;

class Threads extends BaseThreads {
    private JButton startButton;
    private JButton stopButton;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSlider slider;
    private JPanel rootPanel;

    private Thread thread1;
    private Thread thread2;

    Threads() {
        super();
        initialize();
        setUpView(rootPanel);
        spinner1.addChangeListener(e -> {
            if (thread1 != null) {
                thread1.setPriority((Integer) spinner1.getValue());
            }
        });
        spinner2.addChangeListener(e -> {
            if (thread2 != null) {
                thread2.setPriority((Integer) spinner2.getValue());
            }
        });
    }

    private void initialize() {
        initializeSlider(slider);

        //initializeSpinners
        Integer[] list = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        spinner1.setModel(new SpinnerListModel(list));
        spinner2.setModel(new SpinnerListModel(list));

        //initializeButtons
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        //addListeners
        ActionListener listener = e -> {
            if (e.getSource() == startButton) {
                initializeThreads();
                thread1.start();
                thread2.start();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            } else if (e.getSource() == stopButton) {
                thread1.interrupt();
                thread2.interrupt();
                slider.setValue(50);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        };
        startButton.addActionListener(listener);
        stopButton.addActionListener(listener);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (thread1 != null)
                    thread1.interrupt();
                if (thread2 != null)
                    thread2.interrupt();
                e.getWindow().dispose();
            }
        });
    }

    private void initializeThreads() {
        thread1 = new Thread(new Task(10));
        thread2 = new Thread(new Task(90));

        thread1.setPriority((Integer) spinner1.getValue());
        thread2.setPriority((Integer) spinner2.getValue());

//        System.out.println(thread1.getPriority());
//        System.out.println(thread2.getPriority());
    }

    public static void main(String[] args) {
        new Threads();
    }
}
