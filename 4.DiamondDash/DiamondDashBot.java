import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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
            // TODO: execute in different thread
            searchForDiamondDashLogo();
        }
    }

    private void searchForDiamondDashLogo() {
        Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenshot = robot.createScreenCapture(screenRectangle);
        try {
            BufferedImage diamondDashLogo = ImageIO.read(new File("diamond.dash.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // TODO: search for logo in screenshot

        System.out.println("DONE");
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws AWTException {
        new DiamondDashBot();
    }
}
