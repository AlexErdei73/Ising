import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ising extends Canvas implements Runnable {
  int latticeSize = 100;
  int canvasSize = 400;
  int atomSize = canvasSize / latticeSize;
  int[][] atomStates = new int[latticeSize][latticeSize];
  double T = 10;
  DoubleScroller tempScroller;
  Ising() {
    setSize(canvasSize, canvasSize);
    setBackground(Color.WHITE);
    Frame isingFrame = new Frame("Ising Model");
    Panel canvasPanel = new Panel();
    canvasPanel.add(this);
    isingFrame.add(canvasPanel,BorderLayout.NORTH);
    isingFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
    Panel dataPanel = new Panel();
    Canvas dataCanvas = new Canvas();
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
    buttonsPanel.add(startBtn);
    controlPanel.add(buttonsPanel);
    isingFrame.add(dataPanel, BorderLayout.CENTER);
    isingFrame.add(controlPanel, BorderLayout.SOUTH);
    isingFrame.pack();
    isingFrame.setVisible(true);
    for (int row=0; row<latticeSize; row++) {
      for (int col=0; col<latticeSize; col++) {
        atomStates[row][col] = 1;
      }
    }
    Thread simulationThread = new Thread(this);
    simulationThread.start();
  }
  public static void main(String[] args) {
    new Ising();
  }
  @Override
  public void paint(Graphics g) {
    for (int row=0; row<latticeSize; row++) {
      for (int col=0; col<latticeSize; col++) {
        Color color = Color.blue;
        if (atomStates[row][col] == 1) color = Color.yellow;
        g.setColor(color);
        g.fill3DRect(atomSize * row, atomSize * col, atomSize, atomSize, false);
      }
    }
  }
  //Override update method to stop Ising to keep on repainting background
  @Override
  public void update(Graphics g) {
    paint(g);
  }
  private double getEnergyDifference(int row, int col) {
    int[] rowIncrements = {0,-1,0,1};
    int[] colIncrements = {-1,0,1,0};
    double result = 0;
    int neighbourRow, neighbourCol, neighbour;
    for (int neighbourIndex=0; neighbourIndex<4; neighbourIndex++) {
      neighbourRow = row + rowIncrements[neighbourIndex];
      neighbourCol = col + colIncrements[neighbourIndex];
      if (neighbourRow < 0 || neighbourCol < 0 || neighbourRow == latticeSize || neighbourCol == latticeSize) neighbour = 0;
        else neighbour = atomStates[neighbourRow][neighbourCol];
      result += 2 * neighbour * atomStates[row][col];
    }
    return result;
  }
  public void simulationStep() {
    int row = (int) Math.floor(Math.random() * latticeSize);
    int col = (int) Math.floor(Math.random() * latticeSize);
    double eChange = getEnergyDifference(row, col);
    if ((eChange <= 0) || (Math.random() < Math.exp(-eChange/T))) atomStates[row][col] *= -1;
  }
  @Override
  public void run() {
    while (true) {
      for (int n=0; n<2000; n++) {
        simulationStep();
      }
      this.repaint();
      T = tempScroller.getValue();
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {}
    }
  }
}
