import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    public Game() {
        add(new LiveGame());
        setTitle("Game of live");
        Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
        setIconImage(icon);
        setSize(1920,1080);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}