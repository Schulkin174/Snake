import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import static java.awt.event.KeyEvent.*;

// класс JFrame представляет собой окно с рамкой и строкой заголовка (с кнопками «Свернуть», «Во весь экран» и «Закрыть»).
public class GamePanel extends JPanel implements ActionListener {

    // создаю конструктор игровой панели

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; // количество квадратиков-полей для змея
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // при необходимости, поменять на L(left), U(up), D(down)
    boolean running = false;
    Timer timer;
    Random random;
    private boolean isPaused = false; // пауза



    // реализую конструктор игровой панели
    GamePanel(){
        random = new Random();
        // устанавливаю размер игрового окна
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black); // при необходимости, заменить цвет поля
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();

    }
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    // запускаю компонент цвета бэкграунда
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g); // Передаю объект Graphics в метод draw
    }
    public void draw(Graphics g){

        if(running){ // если змея продолжает движение, выполняется цикл
        for(int i=0; i<SCREEN_HEIGHT/UNIT_SIZE; i++){ // рисую квадраты
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }
        g.setColor(Color.red); // устанавливаю цвет на яблоко. Установку расположения яблока (рандом) смотри в newApple
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // допустим, что овал - наиболее приятный вариант с точки зрения UI

        for (int i=0; i<bodyParts; i++){ // создаю тело змея
            if(i==0){
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            else{
             //   g.setColor(new Color(45, 180, 0)); // выставляю немного другой оттенок для отличия головы и тела змея. Эстетический нюанс.
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // или же убираем моно-цвет и делаем змею поярче вот так...
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // 255 - стандартный диапазон для цветовой модели RGB
                }
            }
            g.setColor(Color.red); // оформляю "табло", ведущее счет съеденным яблокам
            g.setFont(new Font("Ink Free", Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont()); // без метрики шрифт отображаться не будет
            g.drawString("Съеденных яблок: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Съеденных яблок: " + applesEaten))/2, g.getFont().getSize());
        }
        else { // если змея останавливается, включаю метод gameOver
            gameOver(g);
        }

    }
    public void newApple(){
        appleX = random.nextInt((int) (SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;

    }
    public void move(){
        for (int i = bodyParts; i>0; i--){ // реализую движение змея (bodyParts = всех частей змея)
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){ // реализую направление движения змея. Основа - система координат по вертикали и горизонтали (X и Y)
            case 'U':
                y[0] = y[0] -  UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] +  UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] -  UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] +  UNIT_SIZE;
                break;
        }
    }
    public void checkApple(){

        if((x[0] == appleX) && y[0] == appleY){ // x[0] - расположение головы змеи
            bodyParts++; // съеденные яблочки в рост змеи
            applesEaten++;
            newApple();

        }

    }
    public void checkCollisions(){
        // чекаем не врезалась ли голова змея в его тело(хвост)
        for (int i = bodyParts; i>0; i--){
            if((x[0] == x[i]) && y[0] == y[i]){
                running = false;
            }
        }
        // чекаем не врезалась ли голова в левый борт
        if (x[0] < 0){
            running = false;
        }
        // чекаем не врезалась ли голова в правый борт
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        // чекаем не врезалась ли голова в верхний борт
        if (y[0] < 0) {
            running = false;
        }
        // чекаем не врезалась ли голова в нижний борт
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if(!running){
            timer.stop(); // останавливаем таймер - змя останавливается при столкновении
        }
    }
    public void gameOver(Graphics g){
        // добавляю табло с подсчетами съеденных яблок на экран геймовера
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metrics1 = getFontMetrics(g.getFont()); // без метрики шрифт отображаться не будет
        g.drawString("Съеденных яблок: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Съеденных яблок: " + applesEaten))/2, g.getFont().getSize());
        // прописываю текст gameOver-а
        g.setColor(Color.red); // пускай будет красный
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont()); // без метрики шрифт отображаться не будет
        g.drawString("Аля-улю, гони гусей...", (SCREEN_WIDTH - metrics2.stringWidth("Аля-улю, гони гусей..."))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) { // Перед обработкой событий, проверяем не включена ли пауза, иначе программа не понимает когда пауза есть, а когда ее нет
            if (running) {
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){ // можно было бы использовать и if-else для выбора команд для кнопок: switch будет читабельнее с учетом вложенных if-else
                case VK_LEFT: // VK - "Virtual Key" (виртуальная клавиша) - часть класса KeyEvent, обработчик событий клавиш
                case KeyEvent.VK_A: // добавляю в обработчик возможность управления клавишами A, D, W, S: сопоставляю клавиши с направлением обработчика
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case VK_RIGHT:
                case KeyEvent.VK_D:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case VK_UP:
                case KeyEvent.VK_W:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case VK_DOWN:
                case KeyEvent.VK_S:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
                case VK_SPACE: // для паузы использую пробел как наиболее удобную для этого клавишу
                    isPaused = !isPaused; // Инвертируем (true меняем на false и наоборот)состояние паузы
                    break;
            }
        }
    }

    // добавить в игру музыку
    // добавить уровни с увеличением скорости
    // в принципе, можно добавить и авторизацию, чтобы сохранять счет для каждого игрока
}