import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ising extends Canvas implements Runnable {
  Ising() {
    setSize(400, 400);
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
    for (int i=0; i<20; i++) {
      for (int j=0; j<20; j++) {
        Color color = Color.blue;
        if ((i + j) % 2==0) color = Color.yellow;
        g.setColor(color);
        g.fill3DRect(20 * i, 20 * j, 20, 20, false);
      }
    }
  }
  @Override
  public void run() {

  }
}
