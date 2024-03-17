import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ising extends Canvas implements Runnable {
  int latticeSize = 20;
  int canvasSize = 400;
  int atomSize = canvasSize / latticeSize;
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
  }
  public static void main(String[] args) {
    new Ising();
  }
  @Override
  public void paint(Graphics g) {
    for (int i=0; i<latticeSize; i++) {
      for (int j=0; j<latticeSize; j++) {
        Color color = Color.blue;
        if ((i + j) % 2==0) color = Color.yellow;
        g.setColor(color);
        g.fill3DRect(atomSize * i, atomSize * j, atomSize, atomSize, false);
      }
    }
  }
  @Override
  public void run() {

  }
}
