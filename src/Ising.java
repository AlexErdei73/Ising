import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ising extends Canvas implements Runnable {
  int latticeSize = 20;
  int canvasSize = 400;
  int atomSize = canvasSize / latticeSize;
  int[][] atomStates = new int[latticeSize][latticeSize];
  int count = 0;
  Ising() {
    setSize(canvasSize, canvasSize);
    setBackground(Color.WHITE);
    Frame isingFrame = new Frame("Ising Model");
    isingFrame.setLayout(new GridLayout(0, 1));
    isingFrame.add(this,BorderLayout.CENTER);
    isingFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
    isingFrame.pack();
    isingFrame.setVisible(true);
    for (int row=0; row<latticeSize; row++) {
      for (int col=0; col<latticeSize; col++) {
        if ((row + col) % 2==0) atomStates[row][col] = 1;
          else atomStates[row][col] = -1;
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
  public void simulationStep() {
    System.out.println(count);
    count++;
  }
  @Override
  public void run() {
    while (true) {
      for (int n=0; n<100; n++) {
        simulationStep();
      }
    }
  }
}
