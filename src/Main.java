import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BeeSimulation {
    public static void main(String[] args) {
        int N = 5; // Кількість секунд між змінами напрямку руху трутнів
        double V = 1.0; // Швидкість руху бджіл і трутнів

        BeeWorkerThread beeWorkerThread = new BeeWorkerThread(V);
        DroneThread droneThread = new DroneThread(V, N);

        beeWorkerThread.start();
        droneThread.start();

        // Створення графічного вікна
        JFrame frame = new JFrame("Bee Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Створення канви для відображення графіки
        BeeSimulationCanvas canvas = new BeeSimulationCanvas();
        canvas.setPreferredSize(new Dimension(800, 600));

        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);

        // Передача посилання на канву до потоків
        beeWorkerThread.setCanvas(canvas);
        droneThread.setCanvas(canvas);
    }

    // Клас канви для відображення графіки
    private static class BeeSimulationCanvas extends JPanel {
        private double beeWorkerX = 0;
        private double beeWorkerY = 0;
        private double droneX = 0;
        private double droneY = 0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int width = getWidth();
            int height = getHeight();

            int beeWorkerPosX = (int) (width / 2 + beeWorkerX * 100);
            int beeWorkerPosY = (int) (height / 2 + beeWorkerY * 100);
            int dronePosX = (int) (width / 2 + droneX * 100);
            int dronePosY = (int) (height / 2 + droneY * 100);

            g.setColor(Color.RED);
            g.fillOval(beeWorkerPosX, beeWorkerPosY, 10, 10);

            g.setColor(Color.BLUE);
            g.fillOval(dronePosX, dronePosY, 10, 10);
        }
    }

    private static class BeeWorkerThread extends Thread {
        private double V;
        private boolean isRunning = true;
        private double beeWorkerX = 0;
        private double beeWorkerY = 0;
        private BeeSimulationCanvas canvas;

        public BeeWorkerThread(double velocity) {
            this.V = velocity;
        }

        public void setCanvas(BeeSimulationCanvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void run() {
            while (isRunning) {
                Random random = new Random();
                double angle = 2 * Math.PI * random.nextDouble();
                double dx = Math.cos(angle);
                double dy = Math.sin(angle);
                beeWorkerX += dx * V;
                beeWorkerY += dy * V;

                if (canvas != null) {
                    canvas.repaint();
                }

                try {
                    Thread.sleep(1000); // Затримка 1 секунда між оновленнями позиції бджіл
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRunning() {
            isRunning = false;
        }
    }

    private static class DroneThread extends Thread {
        private double V;
        private int N;
        private boolean isRunning = true;
        private double droneX = 0;
        private double droneY = 0;
        private BeeSimulationCanvas canvas;

        public DroneThread(double velocity, int n) {
            this.V = velocity;
            this.N = n;
        }

        public void setCanvas(BeeSimulationCanvas canvas) {
            this.canvas = canvas;
        }

        @Override
        public void run() {
            while (isRunning) {
                Random random = new Random();
                // Генерувати новий напрямок руху через N секунд
                if (System.currentTimeMillis() % (N * 1000) == 0) {
                    double angle = 2 * Math.PI * random.nextDouble();
                    double dx = Math.cos(angle);
                    double dy = Math.sin(angle);
                    droneX += dx * V;
                    droneY += dy * V;

                    if (canvas != null) {
                        canvas.repaint();
                    }
                }

                try {
                    Thread.sleep(1000); // Затримка 1 секунда між оновленнями позиції трутнів
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRunning() {
            isRunning = false;
        }
    }
}
