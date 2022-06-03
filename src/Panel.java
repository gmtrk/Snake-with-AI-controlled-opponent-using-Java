import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Panel extends JPanel implements ActionListener {

    static final int GAME_WIDTH = 800;
    static final int GAME_HEIGHT = 800;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (GAME_WIDTH*GAME_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 4;
    int fruitsEaten;
    int fruitX;
    int fruitY;
    char direction = 'R';
    boolean running = false;
    boolean launched = false;
    boolean hidebutton = false;
    Timer time;
    Random random;
    Panel(){
        random = new Random();
        this.setPreferredSize(new Dimension(GAME_WIDTH,GAME_HEIGHT));
        this.setBackground(Color.CYAN);
        this.setFocusable(true);
        this.addKeyListener(new Adapter());
        startGame();
    }
    public void startGame() {
        newFruit();
        time = new Timer(DELAY, this);
        time.start();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(!launched){
            GameMenu(g);
        }
        else{
        if (running) {
            for (int i = 0; i < GAME_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, GAME_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, GAME_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.YELLOW);
            g.fillOval(fruitX, fruitY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Twoj wynik: " +fruitsEaten, GAME_WIDTH/8, g.getFont().getSize());
        }
        else {
            hidebutton = false;
            GameOver(g);
        }
    }
    }
    public void newFruit(){
        fruitX = random.nextInt((int)(GAME_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        fruitY = random.nextInt((int)(GAME_WIDTH/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move() {
        for(int i = bodyParts; i>0; i--){
            x[i] =x[i-1];
            y[i] =y[i-1];
        }
        switch(direction){
            case 'U':
                y[0] = y[0] -UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] +UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] -UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] +UNIT_SIZE;
                break;
        }
    }
    public void checkFruit() {
        if((x[0] == fruitX) && (y[0]==fruitY)){
            bodyParts++;
            fruitsEaten++;
            newFruit();
        }
    }
    public void checkCollisions() {
        //jezeli waz uderzy w samego siebie
        for(int i = bodyParts; i>0;i--){
            if((x[0] == x[i])&&(y[0] ==y[i])){
                running = false;
            }
        }
        //jezeli waz uderzy w krawedzie
        if(x[0]<0 || x[0] >= GAME_WIDTH || y[0]<0 || y[0]>=GAME_HEIGHT){
            running = false;
        }
        if (running == false){
            time.stop();
        }
    }
    public void GameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Koniec Gry", (GAME_WIDTH - metrics.stringWidth("Koniec Gry"))/2, GAME_HEIGHT/2);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Twoj wynik: " +fruitsEaten, (GAME_WIDTH- metrics1.stringWidth("Twoj wynik: " +fruitsEaten))/2, GAME_HEIGHT-g.getFont().getSize());

        JButton b = new JButton("Restart?");
        b.setBounds((GAME_WIDTH/2)-75,(GAME_HEIGHT/2)+25,150,50);
        if(!hidebutton) {
            this.add(b);
            hidebutton = true;
        }
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResetGame();
                startGame();
                repaint();
                running = true;
                b.setVisible(false);
            }
        });
    }
    public void GameMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Snake na javie", (GAME_WIDTH - metrics.stringWidth("Snake na javie"))/2, GAME_HEIGHT/2);
        JButton b = new JButton("Rozpocznij gre!");
        b.setBounds((GAME_WIDTH/2)-75,(GAME_HEIGHT/2)+25,150,50);
        if(!hidebutton) {
            this.add(b);
            hidebutton = true;
        }
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launched = true;
                running = true;
                b.setVisible(false);
            }
        });

    }
    public void ResetGame(){
        for(int i =0; i<bodyParts; i++){
            x[i] = 0;
            y[i] =0;
        }
        bodyParts = 4;
        fruitsEaten = 0;
        direction = 'R';

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkFruit();
            checkCollisions();
        }
        repaint();
    }

    public class Adapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction!= 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction!= 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction!= 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction!= 'U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
