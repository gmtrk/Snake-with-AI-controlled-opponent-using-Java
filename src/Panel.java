import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Panel extends JPanel implements ActionListener {

    static final int GAME_WIDTH = 800;
    static final int GAME_HEIGHT = 800;
    static final int UNIT_SIZE = 80;
    static final int GAME_UNITS = (GAME_WIDTH*GAME_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 4;
    int fruitsEaten;
    //coordinates for AI snake
    final int AIx[] = new int[GAME_UNITS];

    final int AIy[] = new int[GAME_UNITS];
    int AIbodyParts = 5;
    int AIfruitsEaten;
    int fruitX[] = new int [2];
    int newX[] = new int [2];
    int fruitY[] = new int [2];;
    int newY[] = new int [2];;
    char direction = 'R';
    char AIdirection = 'L';
    boolean running = false;
    boolean launched = false;
    boolean hidebutton = false;
    boolean isfruit;
    boolean won = false;
    Timer time;
    Random random;
    Panel(){
        AIx[0] = GAME_WIDTH-UNIT_SIZE;
        AIy[0] = GAME_HEIGHT-UNIT_SIZE;
        random = new Random();
        this.setPreferredSize(new Dimension(GAME_WIDTH,GAME_HEIGHT));
        this.setBackground(Color.CYAN);
        this.setFocusable(true);
        this.addKeyListener(new Adapter());
        startGame();
    }
    public void startGame() {
        newFruit(0);
        newFruit(1);
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
            g.fillOval(fruitX[0], fruitY[0], UNIT_SIZE, UNIT_SIZE);
            g.fillOval(fruitX[1], fruitY[1], UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            for (int i = 0; i < AIbodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.RED);
                    g.fillRect(AIx[i], AIy[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(180, 45, 0));
                    g.fillRect(AIx[i], AIy[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Twoj wynik: " +fruitsEaten, UNIT_SIZE, g.getFont().getSize());
            g.drawString("Wynik AI: " +AIfruitsEaten, 7*UNIT_SIZE, g.getFont().getSize());
        }
        else {
            hidebutton = false;
            if(won){
                GameWon(g);
            }
            else{
            GameOver(g);
        }
        }
    }
    }
    public boolean isTaken(int a, int b){

        for(int i = 0; i<bodyParts; i++){
            if(x[i] == a)
            {

                return true;
            }
            if(y[i] == b)
            {

                return true;
            }
        }
        return false;
    }
    public void newFruit(int fruit){
        newX[fruit] =ThreadLocalRandom.current().nextInt(0,(GAME_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        newY[fruit] =ThreadLocalRandom.current().nextInt(0,(GAME_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        //nie wier czemu to nie dziala jestem chyba glupi i nie rozumiem pewnych rzeczy
       /* isfruit=isTaken(newX[fruit],newY[fruit]);
        while(isfruit){
            newX[fruit] = ThreadLocalRandom.current().nextInt(0,(GAME_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            newY[fruit] =ThreadLocalRandom.current().nextInt(0,(GAME_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
            isfruit=isTaken(newX[fruit],newY[fruit]);
            if (!isfruit)
            {
            break;
            }
        }*/
        fruitX[fruit] = newX[fruit];
        fruitY[fruit] = newY[fruit];
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
    public void AImove(){
        //dla AI
        for(int i = AIbodyParts; i>0; i--){
            AIx[i] =AIx[i-1];
            AIy[i] =AIy[i-1];
        }
        switch(AIdirection){
            case 'U':
                AIy[0] = AIy[0] -UNIT_SIZE;
                break;
            case 'D':
                AIy[0] = AIy[0] +UNIT_SIZE;
                break;
            case 'L':
                AIx[0] = AIx[0] -UNIT_SIZE;
                break;
            case 'R':
                AIx[0] = AIx[0] +UNIT_SIZE;
                break;
        }
    }

    public void checkFruit() {
        if((x[0] == fruitX[0]) && (y[0]==fruitY[0])){
            bodyParts++;
            fruitsEaten++;
            newFruit(0);
        }
        if((x[0] == fruitX[1]) && (y[0]==fruitY[1])){
            bodyParts++;
            fruitsEaten++;
            newFruit(1);
        }
        if((AIx[0] == fruitX[0]) && (AIy[0]==fruitY[0])){
            AIbodyParts++;
            AIfruitsEaten++;
            newFruit(0);
        }
        if((AIx[0] == fruitX[1]) && (AIy[0]==fruitY[1])){
            AIbodyParts++;
            AIfruitsEaten++;
            newFruit(1);
        }
    }
    public void checkCollisions() {
        //jezeli waz uderzy w samego siebie
        for(int i = bodyParts; i>0;i--){
            if((x[0] == x[i])&&(y[0] ==y[i])){
                running = false;
            }
        }
        //jezeli waz uderzy w AI
        for(int i = bodyParts; i>0;i--){
            if((x[0] == AIx[i])&&(y[0] ==AIy[i])){
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
        //jezeli AI waz uderzy w siebie
        for(int i = bodyParts; i>0;i--){
            if((AIx[0] == AIx[i])&&(AIy[0] ==AIy[i])){
                won = true;
                running = false;
            }
        }
        //jezeli AI waz uderzy w gracza
        for(int i = bodyParts; i>0;i--){
            if((AIx[0] == x[i])&&(AIy[0] ==y[i])){
                won = true;
                running = false;
            }
        }
        //jezeli AI waz uderzy w krawedzie
        if(AIx[0]<0 || AIx[0] >= GAME_WIDTH || AIy[0]<0 || AIy[0]>=GAME_HEIGHT){
            won = true;
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
    public void GameWon(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Wygrales", (GAME_WIDTH - metrics.stringWidth("Wygrales"))/2, GAME_HEIGHT/2);

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
        for(int i =0; i<AIbodyParts; i++){
            AIx[i] = GAME_WIDTH-UNIT_SIZE;
            AIy[i] = GAME_HEIGHT-UNIT_SIZE;
        }
        won = false;
        AIbodyParts= 4;
        bodyParts = 4;
        AIfruitsEaten = 0;
        fruitsEaten = 0;
        direction = 'R';
        AIdirection = 'L';

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            AImove();
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
