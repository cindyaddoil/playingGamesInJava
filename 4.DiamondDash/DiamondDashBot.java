import java.util.List;
import java.io.File;
import java.io.IOException;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.awt.event.InputEvent;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.JLabel;

class DiamondDashBot extends SwingWorker<Void, String> {
    private final static int GRID_ROWS = 9;
    private final static int GRID_COLUMNS = 10;
    private final static int BRICK_WIDTH = 40;
    private final static int BRICK_HEIGHT = 40;
    private final static int SAMPLE_OFFSET = 5;
    private final static double SENSITIVITY = 0.7;
    private final static int COUNTER_THRESHOLD = 20;

    private final Robot robot;
    private final JLabel botStateLabel;
    private final BrickColor grid[][];
    private final int counter[][];

    public DiamondDashBot(JLabel botStateLabel) {
        this.robot = createRobot();
        this.botStateLabel = botStateLabel;
        this.grid = new BrickColor[GRID_ROWS][GRID_COLUMNS];
        this.counter = new int[GRID_ROWS][GRID_COLUMNS];

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                grid[row][col] = BrickColor.GREY;
                counter[row][col] = 0;
            }
        }
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
        Location topLeftBrick = new Location(diamondDashLogoLocation.getX() - 569, diamondDashLogoLocation.getY() + 145);

        publish("Let's play!");
        mouseClick(playButtonLocation); // focus window under cursor
        mouseClick(playButtonLocation); // actually press the Play button

        Thread.sleep(5000);

        while (true) {
            if (isCancelled()) break;

            Rectangle gridRectangle = new Rectangle(topLeftBrick.getX() - 20, topLeftBrick.getY() - 20, BRICK_WIDTH * GRID_COLUMNS, BRICK_HEIGHT * GRID_ROWS);
            BufferedImage gridScreenshot = robot.createScreenCapture(gridRectangle);

            updateGridState(gridScreenshot);
            updateGridCounters();

            GridPosition positionToClick = findCluster();
            Location pointToClick = null;

            if (positionToClick != null) {
                pointToClick = positionToLocation(topLeftBrick, positionToClick);
            }

            if (pointToClick != null) {
                mouseClick(pointToClick);
            }

            try {
                Thread.sleep(50);
            } catch (Exception e) {}
        }

        return null;
    }

    private Location positionToLocation(Location topLeftBrick, GridPosition positionToClick) {
        return new Location(topLeftBrick.getX() + positionToClick.getCol() * BRICK_WIDTH,
                            topLeftBrick.getY() + positionToClick.getRow() * BRICK_HEIGHT);
    }

    @Override
    protected void process(final List<String> chunks) {
        botStateLabel.setText(chunks.get(chunks.size() - 1));
    }

    private GridPosition findCluster() {
        // priority to diamonds
        GridPosition diamondLocation = findDiamond();

        if (diamondLocation != null) {
            return diamondLocation;
        }

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (grid[row][col] == BrickColor.GREY) continue;

                if (isStraightCluster(row, col) || isLCluster(row, col)) {
                    return new GridPosition(row, col);
                }
            }
        }

        return null;
    }

    private boolean isLCluster(int row, int col) {
        BrickColor brickColor = grid[row][col];

        if (row > 0 && col < GRID_COLUMNS - 1) {
            if (grid[row-1][col] == brickColor && grid[row][col+1] == brickColor)
                return true;
        }

        if (row < GRID_ROWS - 1 && col < GRID_COLUMNS - 1) {
            if (grid[row+1][col] == brickColor && grid[row][col+1] == brickColor)
                return true;
        }

        if (row < GRID_ROWS - 1 && col > 0) {
            if (grid[row][col-1] == brickColor && grid[row+1][col] == brickColor)
                return true;
        }

        if (row > 0 && col > 0) {
            if (grid[row-1][col] == brickColor && grid[row][col-1] == brickColor)
                return true;
        }

        return false;
    }

    private boolean isStraightCluster(int row, int col) {
        return isTopDownStraight(row, col) || isLeftRightStraight(row, col);
    }

    private boolean isTopDownStraight(int row, int col) {
        if (row == 0 || row == GRID_ROWS - 1) return false;

        return grid[row-1][col] == grid[row][col] && grid[row+1][col] == grid[row][col];
    }

    private boolean isLeftRightStraight(int row, int col) {
        if (col == 0 || col == GRID_COLUMNS - 1) return false;

        return grid[row][col-1] == grid[row][col] && grid[row][col+1] == grid[row][col];
    }

    private GridPosition findDiamond() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (grid[row][col] == BrickColor.DIAMOND) {
                    return new GridPosition(row, col);
                }
            }
        }

        return null;
    }

    private void updateGridState(BufferedImage gridScreenshot) {
        int r, g, b;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                int red = 0, green = 0, blue = 0, yellow = 0, purple = 0;

                int brick_x = BRICK_WIDTH * col + BRICK_WIDTH / 2;
                int brick_y = BRICK_HEIGHT * row + BRICK_HEIGHT / 2;

                for (int x_offset = -SAMPLE_OFFSET; x_offset <= SAMPLE_OFFSET; x_offset++) {
                    for (int y_offset = -SAMPLE_OFFSET; y_offset <= SAMPLE_OFFSET; y_offset++) {
                        Color color = new Color(gridScreenshot.getRGB(brick_x + x_offset, brick_y + y_offset));
                        r = color.getRed();
                        g = color.getGreen();
                        b = color.getBlue();

                        if (r > g && g > b && r - b > 70) yellow++;
                        else if (r > b && b > g && r - g > 70) red++;
                        else if (g > r && r > b && g - b > 70) green++;
                        else if (b > r && r > g && b -g > 70) purple++;
                        else if (b > g && g > r && b - r > 70) blue++;

                        int best_suited_color = Math.max(yellow, Math.max(red, Math.max(green, Math.max(purple, blue))));

                        if (best_suited_color < SENSITIVITY * 4 * SAMPLE_OFFSET * SAMPLE_OFFSET) {
                            grid[row][col] = BrickColor.GREY;
                        } else if (best_suited_color == yellow) {
                            grid[row][col] = BrickColor.YELLOW;
                        } else if (best_suited_color == red) {
                            grid[row][col] = BrickColor.RED;
                        } else if (best_suited_color == green) {
                            grid[row][col] = BrickColor.GREEN;
                        } else if (best_suited_color == purple) {
                            grid[row][col] = BrickColor.PURPLE;
                        } else if (best_suited_color == blue) {
                            grid[row][col] = BrickColor.BLUE;
                        } else {
                            grid[row][col] = BrickColor.GREY;
                        }
                    }
                }
            }
        }
    }

    private void updateGridCounters() {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLUMNS; col++) {
                if (grid[row][col] == BrickColor.GREY) {
                    counter[row][col]++;
                    if (counter[row][col] >= COUNTER_THRESHOLD) {
                        grid[row][col] = BrickColor.DIAMOND;
                        counter[row][col] = 0;
                    }
                } else {
                    counter[row][col] = 0;
                }
            }
        }
    }

    private void mouseClick(Location location) {
        robot.mouseMove(location.getX(), location.getY());
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private enum BrickColor {
        RED,
        GREEN,
        BLUE,
        YELLOW,
        PURPLE,
        GREY,
        DIAMOND
    }

    private class GridPosition {
        private int row, col;

        public GridPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
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
}
