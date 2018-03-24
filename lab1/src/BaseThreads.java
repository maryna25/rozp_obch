import javax.swing.*;

public class BaseThreads extends JFrame {
    private JPanel rootPanel;
    private JSlider slider;

    class Task implements Runnable {
        private int value;

        Task(int value) {
            this.value = value;
        }

        private synchronized void setValue() {
            slider.setValue(value);
            repaint();
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                setValue();
            }
        }
    }

    BaseThreads() {
        super("Lab1");
    }

    void setUpView(JPanel rootPanel) {
        this.rootPanel = rootPanel;
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        this.setBounds(500, 200, 500, 350);
        setContentPane(rootPanel);
    }

    void initializeSlider(JSlider slider) {
        this.slider = slider;
        slider.setMinimum(0);
        slider.setMaximum(100);
        slider.setValue(50);
    }


}
