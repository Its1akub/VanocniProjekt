import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LiveGame extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    private final int xPanel = 1620;
    private final int yPanel = 1010;
    private final int size = 10;
    private final int xWidth = xPanel / size;
    private final int yHeight = yPanel / size;
    private int initial = 0,countShift = 0,countOfCells = 0,generation = 0;
    private boolean gameRuntime = false;
    private final int[][] cells = new int[xWidth][yHeight];
    private final int[][] afterCells = new int[xWidth][yHeight];
    private final Timer time;
    private final JLabel numberOfCells;
    private final JLabel numberOfShift;
    private final JLabel speed;
    private final JLabel genNumber;
    private final JLabel gameplay;

    public LiveGame() {
        setSize(xPanel, yPanel);
        setLayout(null);
        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        time = new Timer(80, this);
        JLabel label = new JLabel("<html>Game of live<BR>SHIFT - erase<BR>Q - reset<BR>C - clear<BR>R - random<BR>S - start<BR>A - stop<BR>"
                + "+/ 1 - speed 1<BR>ě / 2 - speed 10<BR>š / 3 - speed 80<BR>č / 4 - speed 300<BR>ř / 5 - speed 1000</html>");
        label.setForeground(Color.WHITE);
        label.setBounds(1700, 20, 200, 500);
        add(label);
        gameplay = new JLabel("GAME : paused");
        gameplay.setForeground(Color.WHITE);
        gameplay.setBounds(1700, 450, 150, 25);
        add(gameplay);
        genNumber = new JLabel("generation: " + generation);
        genNumber.setBounds(1700, 500, 150, 25);
        genNumber.setForeground(Color.WHITE);
        add(genNumber);
        numberOfCells= new JLabel();
        numberOfCells.setBounds(1700, 550, 150, 25);
        numberOfCells.setForeground(Color.WHITE);
        add(numberOfCells);
        numberOfShift = new JLabel();
        numberOfShift.setBounds(1700, 600, 150, 25);
        numberOfShift.setForeground(Color.WHITE);
        add(numberOfShift);
        speed = new JLabel();
        speed.setBounds(1700, 650, 150, 25);
        speed.setForeground(Color.WHITE);
        add(speed);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        grid(g);
        display(g);

    }
    /**
     * Clears all the cells in the grid.
     */
    public void clear() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < yHeight; y++) {
                afterCells[x][y] = 0;
            }
        }
    }
    /**
     * Renders a grid on the graphics object.
     * @param  g  the Graphics object to render the grid on
     */
    private void grid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < cells.length + 1; i++) {
            g.drawLine(0, i * size, xPanel, i * size);//rows
            g.drawLine(i * size, 0, i * size, yPanel);//columns
        }
    }
    /**
     * Spawns random values in the afterCells array based on a probability of 1/5.
     */
    private void spawn() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < yHeight; y++) {
                if ((int) (Math.random() * 5) == 0) {
                    afterCells[x][y] = 1;
                }
            }
        }
    }
    /**
     * Display the cells on the screen using the given graphics object.
     * @param  g  the graphics object used to draw the cells
     */
    private void display(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        copyArray();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < yHeight; y++) {
                if (cells[x][y] == 1) {
                    countOfCells++;
                    g.fillRect(x * size, y * size, size, size);
                }
            }
        }
        numberOfCells.setText("Number of cells: " + countOfCells);
        countOfCells = 0;
        if (countShift == 1){
            numberOfShift.setText("mode: erase");
        }else {
            numberOfShift.setText("mode: draw");
        }
        speed.setText("speed: " + time.getDelay()+" ms");
        genNumber.setText("generation: " +generation);
        String status;
        if (gameRuntime) {
            status = "running";
        }else {
            status = "paused";
        }
        gameplay.setText("GAME : "+ status);
    }
    /**
     * Copies the elements of the 'afterCells' array to the 'cells' array using the
     * System.arraycopy() method.
     */
    private void copyArray() {
        for (int x = 0; x < cells.length; x++) {
            System.arraycopy(afterCells[x], 0, cells[x], 0, yHeight);
        }
    }
    /**
     * Calculates the number of alive neighboring cells for a given cell.
     * @param  x  the x-coordinate of the cell
     * @param  y  the y-coordinate of the cell
     * @return    the number of alive neighboring cells
     */
    private int checkNeighbors(int x, int y) {
        int alive = 0;
        alive += cells[(x + xWidth - 1) % xWidth][(y + yHeight - 1) % yHeight];
        alive += cells[(x + xWidth) % xWidth][(y + yHeight - 1) % yHeight];
        alive += cells[(x + xWidth + 1) % xWidth][(y + yHeight - 1) % yHeight];
        alive += cells[(x + xWidth - 1) % xWidth][(y + yHeight) % yHeight];
        alive += cells[(x + xWidth + 1) % xWidth][(y + yHeight) % yHeight];
        alive += cells[(x + xWidth - 1) % xWidth][(y + yHeight + 1) % yHeight];
        alive += cells[(x + xWidth) % xWidth][(y + yHeight + 1) % yHeight];
        alive += cells[(x + xWidth + 1) % xWidth][(y + yHeight + 1) % yHeight];
        return alive;
    }

    /**
     * This method is called when an action event occurs. It updates the state of the cells in a grid
     * based on the rules of Conway's Game of Life.
     * @param  e  the action event that occurred
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int alive;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < yHeight; y++) {
                alive = checkNeighbors(x, y);
                if (alive == 3) {
                    afterCells[x][y] = 1;
                } else if (alive == 2 && cells[x][y] == 1) {
                    afterCells[x][y] = 1;
                } else {
                    afterCells[x][y] = 0;
                }
            }
        }
        repaint();
        generation+=1;

    }
    /**
     * A method that is called when the mouse is dragged.
     * @param  e The MouseEvent object that contains information about the mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        try {
            if (cells[x][y] == 0 && initial == 0) {
                afterCells[x][y] = 1;
            } else if (cells[x][y] == 1 && initial == 1) {
                afterCells[x][y] = 0;
            }
        } catch (Exception ignored) {
        }
        repaint();
    }

    /**
     * Handles the keyPressed event.
     * @param  e  the KeyEvent object representing the key press
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            spawn();
        } else if (e.getKeyCode() == KeyEvent.VK_C) {
            clear();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            time.start();
            gameRuntime = true;
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            time.stop();
            gameRuntime = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            initial = 1;
            countShift += 1;
        }else if (e.getKeyCode() == KeyEvent.VK_Q){
            generation = 0;
            gameRuntime = false;
            clear();
            time.stop();
        }else if (e.getKeyCode() == 49||e.getKeyCode() == 97){
           time.setDelay(1);
        }else if (e.getKeyCode() == 50||e.getKeyCode() == 98){
            time.setDelay(10);
        }else if (e.getKeyCode() == 51||e.getKeyCode() == 99){
            time.setDelay(80);
        }else if (e.getKeyCode() == 52||e.getKeyCode() == 100){
            time.setDelay(300);
        }else if (e.getKeyCode() == 53||e.getKeyCode() == 101){
            time.setDelay(1000);
        }
        if (countShift == 2) {
            initial = 0;
            countShift = 0;
        }
        repaint();
    }
    //region UNUSED METHODS
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    //endregion
}