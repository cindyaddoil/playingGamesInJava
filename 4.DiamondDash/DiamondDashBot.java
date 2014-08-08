import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class DiamondDashBot extends JFrame implements KeyListener {
    private final Robot robot;
    private final JLabel botState;

    public DiamondDashBot() throws AWTException {
        setTitle("DiamondDash Bot");
        setSize(200, 60);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        botState = new JLabel("Ready", SwingConstants.CENTER);
        add(botState);
        getContentPane().add(botState, BorderLayout.CENTER);

        robot = new Robot();

        addKeyListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("ENTER pressed");
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws AWTException {
        new DiamondDashBot();
    }
}
