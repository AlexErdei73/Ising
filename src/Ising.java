import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Ising extends Canvas implements Runnable {
    int latticeSize = 200;
    int canvasSize = 400;
    int atomSize = canvasSize / latticeSize;
    int[][] atomStates = new int[latticeSize][latticeSize];
    double T = 3;
    DoubleScroller tempScroller;
    boolean isRunning = false;
    private double energy = 0;
    private double magnetization = 0;
    private double sumE = 0;
    private double sumM = 0;
    private double count = 0;
    private Image offScreenImage;

    private synchronized void calcData() {
        energy = 0;
        magnetization = 0;
        for (int row = 0; row < latticeSize; row++) {
            for (int col = 0; col < latticeSize; col++) {
                energy += -0.5 * getEnergyDifference(row, col);
                magnetization += atomStates[row][col];
            }
        }
        energy *= 0.5;
        sumE += energy;
        sumM += magnetization;
        count++;
    }

    private synchronized double getEnergy() {
        return energy;
    }

    private synchronized double getMagnetization() {
        return magnetization;
    }

    private synchronized void resetAverages() {
        sumE = 0;
        sumM = 0;
        count = 0;
    }

    private synchronized double getAvgE() {
        return sumE / count;
    }

    private synchronized double getAvgM() {
        return sumM / count;
    }

    private final Canvas dataCanvas;
    private final DecimalFormat twoDecimal = new DecimalFormat("0.00");

    Ising() {
        setSize(canvasSize, canvasSize);
        setBackground(Color.WHITE);
        Frame isingFrame = new Frame("Ising Model");
        Panel canvasPanel = new Panel();
        canvasPanel.add(this);
        isingFrame.add(canvasPanel, BorderLayout.NORTH);
        isingFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        Panel dataPanel = new Panel();
        dataCanvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                g.drawString("E = " + getEnergy(), 0, 15);
                g.drawString("M = " + getMagnetization(), 0, 30);
                g.drawString("Avg E =" + twoDecimal.format(getAvgE()), 0, 45);
                g.drawString("Avg M = " + twoDecimal.format(getAvgM()), 0, 60);
            }
        };
        dataCanvas.setSize(canvasSize, 105);
        dataCanvas.setBackground(Color.white);
        dataPanel.add(dataCanvas);
        Panel controlPanel = new Panel();
        controlPanel.setLayout(new GridLayout(0, 1));
        tempScroller = new DoubleScroller("Temperature: ", 0.01, 10, 0.01, 3);
        controlPanel.add(tempScroller);
        Panel buttonsPanel = new Panel();
        buttonsPanel.setLayout(new GridLayout(1, 0));
        Button startBtn = new Button("Start");
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (isRunning) {
                    isRunning = false;
                    startBtn.setLabel("Start");
                } else {
                    isRunning = true;
                    startBtn.setLabel("Stop");
                }
            }
        });
        Button resetBtn = new Button("Reset");
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resetAverages();
            }
        });
        Button recordBtn = new Button("Record");
        recordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("E = " + energy);
                System.out.println("M = " + magnetization);
                System.out.println("Avg E = " + twoDecimal.format(getAvgE()));
                System.out.println("Avg M = " + twoDecimal.format(getAvgM()));
            }
        });
        buttonsPanel.add(startBtn);
        buttonsPanel.add(resetBtn);
        buttonsPanel.add(recordBtn);
        controlPanel.add(buttonsPanel);
        isingFrame.add(dataPanel, BorderLayout.CENTER);
        isingFrame.add(controlPanel, BorderLayout.SOUTH);
        isingFrame.pack();
        isingFrame.setVisible(true);
        for (int row = 0; row < latticeSize; row++) {
            for (int col = 0; col < latticeSize; col++) {
                atomStates[row][col] = 1;
            }
        }
        offScreenImage = createImage(canvasSize, canvasSize);
        initialPaint();
        Thread simulationThread = new Thread(this);
        simulationThread.start();
    }

    public static void main(String[] args) {
        new Ising();
    }

    @Override
    public synchronized void paint(Graphics g) {
        g.drawImage(offScreenImage,0,0,canvasSize,canvasSize,this);
    }

    //Override update method to stop Ising to keep on repainting background
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private double getEnergyDifference(int row, int col) {
        int[] rowIncrements = {0, -1, 0, 1};
        int[] colIncrements = {-1, 0, 1, 0};
        double result = 0;
        int neighbourRow, neighbourCol, neighbour;
        for (int neighbourIndex = 0; neighbourIndex < 4; neighbourIndex++) {
            neighbourRow = row + rowIncrements[neighbourIndex];
            neighbourCol = col + colIncrements[neighbourIndex];
            if (neighbourRow < 0 || neighbourCol < 0 || neighbourRow == latticeSize || neighbourCol == latticeSize)
                neighbour = 0;
            else neighbour = atomStates[neighbourRow][neighbourCol];
            result += 2 * neighbour * atomStates[row][col];
        }
        return result;
    }

    private synchronized void paintRectangle(int row, int col) {
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        if (atomStates[row][col] == 1) {
            offScreenGraphics.setColor(Color.yellow);
        } else {
            offScreenGraphics.setColor(Color.blue);
        };
        offScreenGraphics.fillRect(atomSize * row, atomSize * col, atomSize, atomSize);
    }

    private void initialPaint() {
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        for (int row = 0; row < latticeSize; row++) {
            for (int col = 0; col < latticeSize; col++) {
                if (atomStates[row][col] == 1) offScreenGraphics.setColor(Color.yellow);
                else offScreenGraphics.setColor(Color.blue);
                offScreenGraphics.fillRect(atomSize * row, atomSize * col, atomSize, atomSize);
            }
        }
        this.repaint();
    }

    public void simulationStep() {
        int row = (int) Math.floor(Math.random() * latticeSize);
        int col = (int) Math.floor(Math.random() * latticeSize);
        double eChange = getEnergyDifference(row, col);
        if ((eChange <= 0) || (Math.random() < Math.exp(-eChange / T))) {
            atomStates[row][col] *= -1;
            paintRectangle(row, col);
        }
    }

    @Override
    public void run() {
        resetAverages();
        while (true) {
            if (isRunning) {
                for (int n = 0; n < 2000; n++) {
                    simulationStep();
                }
            }
            this.repaint();
            T = tempScroller.getValue();
            if (isRunning) {
                calcData();
                dataCanvas.repaint();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }
    }
}
