import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;

class DiamondDash extends JFrame implements KeyListener {
    private final JLabel botStateLabel;
    private DiamondDashBot diamondDashBot = null;

    public DiamondDash() {
        setTitle("DiamondDash Bot");
        setSize(200, 60);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        botStateLabel = new JLabel("Ready", SwingConstants.CENTER);
        add(botStateLabel);
        getContentPane().add(botStateLabel, BorderLayout.CENTER);

        addKeyListener(this);
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
                if (diamondDashBot != null && !diamondDashBot.isDone()) {
                    diamondDashBot.cancel(true);
                }
            }

            public void windowLostFocus(WindowEvent e) {}
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (diamondDashBot == null || diamondDashBot.isDone()) {
                diamondDashBot = new DiamondDashBot(botStateLabel);
                diamondDashBot.execute();
            } else {
                diamondDashBot.cancel(true);
                botStateLabel.setText("Cancelled");
            }
        }
    }


    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws AWTException {
        new DiamondDash();
    }
}
