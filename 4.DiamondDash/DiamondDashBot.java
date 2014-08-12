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
            botState.setText("Searching for diamond dash window");
            // TODO: execute in different thread
            Location diamondDashLogoLocation = searchForDiamondDashLogo();
            Location playButtonLocation = new Location(diamondDashLogoLocation.getX() - 115, diamondDashLogoLocation.getY() + 385);
        }
    }

    private class Location {
        private int x, y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private Location searchForDiamondDashLogo() {
        Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage diamondDashLogo = null;
        try {
            diamondDashLogo = ImageIO.read(new File("diamond.dash.bmp"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Location logoLocation = null;

        while (true) {
            BufferedImage screenshot = robot.createScreenCapture(screenRectangle);
            boolean found = false;

            for (int screenX = 0; screenX < screenshot.getWidth() - diamondDashLogo.getWidth(); screenX++) {
                for (int screenY = 0; screenY < screenshot.getHeight() - diamondDashLogo.getHeight(); screenY++) {
                    found = true;

                    for (int logoX = 0; logoX < diamondDashLogo.getWidth(); logoX++) {
                        for (int logoY = 0; logoY < diamondDashLogo.getHeight(); logoY++) {
                            if (screenshot.getRGB(screenX + logoX, screenY + logoY) != diamondDashLogo.getRGB(logoX, logoY)) {
                                found = false;
                                logoX = diamondDashLogo.getWidth();
                                break;
                            }
                        }
                    }

                    if (found) {
                        logoLocation = new Location(screenX, screenY);
                        screenX = screenshot.getWidth();
                        break;
                    }
                }
            }

            if (found) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }

        return logoLocation;
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws AWTException {
        new DiamondDashBot();
    }
}
