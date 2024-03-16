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
  public void run() {

  }
}
