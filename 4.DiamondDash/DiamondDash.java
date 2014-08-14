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
import javax.swing.SwingWorker;
import java.util.List;
import java.awt.event.InputEvent;

class DiamondDashBot extends SwingWorker<Void, String> {
    private final Robot robot;
    private final JLabel botStateLabel;

    public DiamondDashBot(JLabel botStateLabel) {
        this.robot = createRobot();
        this.botStateLabel = botStateLabel;
    }

    private Robot createRobot() {
        Robot r = null;

        try {
            r = new Robot();
        } catch (AWTException e) {
            System.out.println(e.getMessage());
        }

        return r;
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish("Searching for diamond dash window");
        Location diamondDashLogoLocation = searchForDiamondDashLogo();

        Location playButtonLocation = new Location(diamondDashLogoLocation.getX() - 115, diamondDashLogoLocation.getY() + 385);
// 1115 200
// 550 347
        //Location fieldStart = new Location(diamondDashLogoLocation.getX() - 565, diamondDashLogoLocation.getY() + 147);
        Location fieldEnd = new Location(diamondDashLogoLocation.getX() - 565 + 9 * 40, diamondDashLogoLocation.getY() + 147);

        //robot.mouseMove(fieldStart.getX(), fieldStart.getY());
        robot.mouseMove(fieldEnd.getX(), fieldEnd.getY());

/*
        publish("Let's play!");
        mouseClick(playButtonLocation);
*/

        return null;
    }

    @Override
    protected void process(final List<String> chunks) {
        botStateLabel.setText(chunks.get(chunks.size() - 1));
    }

    private void mouseClick(Location location) {
        robot.mouseMove(location.getX(), location.getY());
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
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
}

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

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("enter pressed");
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (diamondDashBot == null || diamondDashBot.isDone()) {
                diamondDashBot = new DiamondDashBot(botStateLabel);
                diamondDashBot.execute();
            } else {
                diamondDashBot.cancel(true);
                diamondDashBot = null;
                botStateLabel.setText("Cancelled");
            }
            //botStateLabel.setText("Searching for diamond dash window");
            // TODO: execute in different thread
            //Location diamondDashLogoLocation = searchForDiamondDashLogo();
            //Location playButtonLocation = new Location(diamondDashLogoLocation.getX() - 115, diamondDashLogoLocation.getY() + 385);
        }
    }


    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) throws AWTException {
        new DiamondDash();
    }
}
