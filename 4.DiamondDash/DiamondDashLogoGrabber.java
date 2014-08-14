import java.awt.Robot;
import java.awt.AWTException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

class DiamondDashLogoGrabber {
    private final Robot robot;

    public DiamondDashLogoGrabber() throws AWTException {
        robot = new Robot();
    }

    public void grab() throws IOException {
        Rectangle logoRectangle = new Rectangle(878, 200, 30, 30);
        BufferedImage diamondDashLogo = robot.createScreenCapture(logoRectangle);
        ImageIO.write(diamondDashLogo, "bmp", new File("diamond.dash.bmp"));
    }

    public static void main(String[] args) throws Exception {
        (new DiamondDashLogoGrabber()).grab();
    }
}
