import javax.swing.*;

// создаем зависимость от JFrame для создания главного окна графического интерфейса
public class GameFrame extends JFrame {
    GameFrame(){

        this.add(new GamePanel());
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }
}
