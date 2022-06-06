import javax.sql.PooledConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Thread;
import java.lang.InterruptedException;

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
    //koordynaty dla weza SI
    final int AIx[] = new int[GAME_UNITS];

    final int AIy[] = new int[GAME_UNITS];
    int AIbodyParts = 4;
    int AIfruitsEaten;
    int fruitX[] = new int [2];
    int fruitY[] = new int [2];
    int bugX;
    int bugY;


    int obstacleX[] = new int [(int)GAME_UNITS/1000];
    int obstacleY[] = new int [(int)GAME_UNITS/1000];
    char direction = 'R';
    char AIdirection = 'L';
    boolean running = false;
    boolean launched = false;
    boolean hidebutton = false;
    boolean won = false;
    Timer time;
    Random random;
    /**
     * funckja do inicjalizacji okna gry
     */
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

    /**
     * tworzy nowe owoce oraz poczatkowe przeszkody
     */
    public void startGame() {
        newFruit(0);
        newFruit(1);
        newBug();
        genObstacles();
        time = new Timer(DELAY, this);
        time.start();

    }

    /**
     * funckja pozwalajaca nam rysowac na oknie
     * @param g the <code>Graphics</code> object to protect
     */
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
            //narysuj siatke
            for (int i = 0; i < GAME_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, GAME_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, GAME_WIDTH, i * UNIT_SIZE);
            }
            //narysuj owoce
            g.setColor(Color.YELLOW);
            g.fillOval(fruitX[0], fruitY[0], UNIT_SIZE, UNIT_SIZE);
            g.fillOval(fruitX[1], fruitY[1], UNIT_SIZE, UNIT_SIZE);

            //rysowanie przeszkod
            g.setColor(Color.DARK_GRAY);
            for(int i = 0; i<obstacleX.length; i++) {
                g.fillRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);
            }
            //narysuj weza
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //narysuj przeciwnika
            for (int i = 0; i < AIbodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.RED);
                    g.fillRect(AIx[i], AIy[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(180, 45, 0));
                    g.fillRect(AIx[i], AIy[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //narysuj robaka
            g.setColor(Color.MAGENTA);
            g.fillOval(bugX, bugY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, UNIT_SIZE, UNIT_SIZE);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Twoj wynik: " +fruitsEaten, UNIT_SIZE, g.getFont().getSize());
            g.drawString("Wynik AI: " +AIfruitsEaten, 10*UNIT_SIZE, g.getFont().getSize());
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

    /**
     * sprawdza czy wskazane pole jest puste
     * @param point
     * @param checkFruits
     * @return
     */
    public boolean isFieldEmpty(Point point, boolean checkFruits)
    {
        if (point.x < 0 || point.x >= GAME_WIDTH / UNIT_SIZE || point.y < 0 || point.y >= GAME_HEIGHT / UNIT_SIZE)
            return false;

        if (checkFruits)
        {
            for(int i = 0; i < 2; ++i)
            {
                int logic_x = fruitX[i] / UNIT_SIZE;
                int logic_y = fruitY[i] / UNIT_SIZE;

                if (logic_x == point.x && logic_y == point.y)
                    return false;
            }
        }

        for(int i = 0; i < bodyParts; ++i)
        {
            int logic_x = x[i] / UNIT_SIZE;
            int logic_y = y[i] / UNIT_SIZE;

            if (logic_x == point.x && logic_y == point.y)
                return false;
        }

        for(int i = 0; i < AIbodyParts; ++i)
        {
            int logic_x = AIx[i] / UNIT_SIZE;
            int logic_y = AIy[i] / UNIT_SIZE;

            if (logic_x == point.x && logic_y == point.y)
                return false;
        }
        for(int i = 0; i < obstacleX.length; ++i){
            int logic_x = obstacleX[i] / UNIT_SIZE;
            int logic_y = obstacleY[i] / UNIT_SIZE;
            if (logic_x == point.x && logic_y == point.y)
                return false;
        }

        return true;
    }

    /**
     * zwraca array liste pustych pol
     * @param checkFruits
     * @return
     */
    public ArrayList<Point> getEmptyFields(boolean checkFruits)
    {
        var points = new ArrayList<Point>();
        for (int x = 0; x < GAME_WIDTH/UNIT_SIZE; ++x)
        {
            for (int y = 0; y < GAME_HEIGHT/UNIT_SIZE; ++y)
            {
                var pt = new Point(x, y);
                if (isFieldEmpty(pt, checkFruits))
                    points.add(pt);
            }
        }

        return points;
    }

    /**
     * gdy jeden z owocow zostanie zlapany, zeby na jego miejsce tworzyl sie nowy
     * @param fruit - ktory z owocow nalezy wygenerowac
     */
    public void newFruit(int fruit){
        var fields = getEmptyFields(true);
        if (fields.size() > 0)
        {
            int rand = ThreadLocalRandom.current().nextInt(0, fields.size());
            fruitX[fruit] = fields.get(rand).x * UNIT_SIZE;
            fruitY[fruit] = fields.get(rand).y * UNIT_SIZE;
        }
    }

    /**
     * do tworzenia poruszajacego sie owoca, czyli robaka, co tik robak ma 20% szans na przesuniecie sie na sasiadujacy mu kafelek
     */
    public void newBug(){
        var fields = getEmptyFields(true);
        if (fields.size() > 0)
        {
            int rand = ThreadLocalRandom.current().nextInt(0, fields.size());
            bugX = fields.get(rand).x * UNIT_SIZE;
            bugY = fields.get(rand).y * UNIT_SIZE;
        }
    }

    /**
     * generowanie losowych przeszkod na planszy
     */
    public void genObstacles(){
        var fields = getEmptyFields(true);
            for(int i = 0; i<obstacleX.length; i++) {
                int rand = ThreadLocalRandom.current().nextInt(0, fields.size());
                obstacleX[i] = fields.get(rand).x * UNIT_SIZE;
                obstacleY[i] = fields.get(rand).y * UNIT_SIZE;
            }

    }

    /**
     * poruszanie sie wezem przez gracza
     */
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
    public class moveSnake extends Thread {
        public void run() {
            move();
            checkCollisions();
            checkFruit();
        }
    }

    /**
     * poruszanie sie wezem przez AI, wybierze najlepsza droge do owoca omijajac przy tym przeszkody
     */
   public void AImove(){
        //dla AI
        Point pos = new Point(AIx[0]/UNIT_SIZE, AIy[0]/UNIT_SIZE);

        char[] directions = { 'U', 'D', 'L', 'R' };
        Point[] targetPos = new Point[4];

        for (int i = 0; i < 4; ++i)
        {
            var pt = pos.move(directions[i]);
            targetPos[i] = isFieldEmpty(pt, false) ? pt : null;
        }

        Point fruit1 = new Point(fruitX[0]/UNIT_SIZE, fruitY[0]/UNIT_SIZE);
        Point fruit2 = new Point(fruitX[1]/UNIT_SIZE, fruitY[1]/UNIT_SIZE);
        Point bug = new Point(bugX/UNIT_SIZE, bugY/UNIT_SIZE);

        Point target = null;
        int targetDist = 0;
        char targetDir = AIdirection;
        for (int i = 0; i < 4; ++i)
        {
            if (targetPos[i] != null) {

                int dist = Math.min(targetPos[i].getDistance(fruit1), Math.min(targetPos[i].getDistance(fruit2),targetPos[i].getDistance(bug)));
                if (target == null || targetDist > dist) {
                    target = targetPos[i];
                    targetDist = dist;
                    targetDir = directions[i];
                }
            }
        }

        AIdirection = targetDir;

        for(int i = AIbodyParts; i>0; i--)
        {
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
        public class moveAI extends Thread {
            public void run() {
                AImove();
                checkCollisions();
                checkFruit();
                }
            }

    /**
     * poruszanie się robaka
     */
    public void moveBug(){
        int rand = ThreadLocalRandom.current().nextInt(0, 20);
        int _bugX = bugX;
        int _bugY = bugY;
        var pt = new Point(_bugX, _bugY);

        switch(rand){
            case 0:
                if((bugY - UNIT_SIZE)>0) {
                    bugY = bugY - UNIT_SIZE;
                }
                break;
            case 1:
                if((bugY + UNIT_SIZE)<GAME_HEIGHT) {
                    bugY = bugY + UNIT_SIZE;
                }
                break;
            case 2:
                if((bugX - UNIT_SIZE)>0) {
                    bugX = bugX - UNIT_SIZE;
                }
                break;
            case 3:
                if((bugX + UNIT_SIZE)<GAME_WIDTH) {
                    bugX = bugX + UNIT_SIZE;
                }

                break;
            default:
                break;
        }
    }
    public class BugMove extends Thread{
        public void run(){
            moveBug();
            checkCollisions();
            checkFruit();
        }
    }

    /**
     * sprawdzanie czy weszlismy w kontakt z owocem/robakiem
     */
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
        if((x[0] == bugX) && (y[0]==bugY)){
            bodyParts+=3;
            fruitsEaten+=3;
            newBug();
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
        if((AIx[0] == bugX) && (AIy[0]==bugY)){
            AIbodyParts+=3;
            AIfruitsEaten+=3;
            newBug();
        }
    }

    /**
     * sprawdzanie kolizji gracza oraz AI
     */
    public void checkCollisions() {
        //jezeli waz uderzy w samego siebie
        for(int i = bodyParts; i>0;i--){
            if((x[0] == x[i])&&(y[0] ==y[i])){
                if(x[0] == 0 && y[0] == 0){

                }else {
                    running = false;
                }
            }
        }
        //jezeli waz uderzy w AI
        for(int i = AIbodyParts; i>0;i--){
            if((x[0] == AIx[i])&&(y[0] ==AIy[i])){
                running = false;
            }
        }
        //jezeli waz uderzy w krawedzie
        if(x[0]<0 || x[0] >= GAME_WIDTH || y[0]<0 || y[0]>=GAME_HEIGHT){
            running = false;
        }
        //jezeli waz uderzy w przeszkode
        for(int i = 0; i<obstacleX.length; i++){
            if(x[0] == obstacleX[i] && y[0] ==obstacleY[i]){
                running = false;
            }
            if(AIx[0] == obstacleX[i] && AIy[0] ==obstacleY[i]){
                won = true;
                running = false;
            }
        }
        if (running == false){
            time.stop();
        }
        //jezeli AI waz uderzy w siebie
        for(int i = AIbodyParts; i>0;i--){
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

    /**
     * rysowanie ekranu w przypadku przegranej
     * @param g
     */
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

    /**
     * rysowanie ekranu w przypadku zwyciestwa
     * @param g
     */
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

    /**
     * rysowanie glownego menu
     * @param g
     */
    public void GameMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Snake na javie", (GAME_WIDTH - metrics.stringWidth("Snake na javie"))/2, GAME_HEIGHT/2);
        g.setFont(new Font("Arial",Font.BOLD, 20));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Zasady:", (GAME_WIDTH - metrics.stringWidth("Zasady"))/2, 3*GAME_HEIGHT/4);
        g.drawString("Zbieraj owoce oraz robaki, manewruj miedzy przeszkodami, pokonaj przeciwnika!", (GAME_WIDTH - metrics.stringWidth("Zbieraj owoce oraz robaki, manewruj miedzy przeszkodami, pokonaj przeciwnika!"))/2, 25+3*GAME_HEIGHT/4);
        g.drawString("Zolte owoce sa warte 1 punkt, robaki w kolorze magenta sa warte 3 punkty", (GAME_WIDTH - metrics.stringWidth("Zolte owoce sa warte 1 punkt, robaki w kolorze magenta sa warte 3 punkty"))/2, 50+3*GAME_HEIGHT/4);
        g.drawString("Uwaga! Robaki potrafia wskoczyc na przeszkody, a nawet moga ujezdzac weze!", (GAME_WIDTH - metrics.stringWidth("Uwaga! Robaki potrafia wskoczyc na przeszkody, a nawet moga ujezdzac weze!"))/2, 75+3*GAME_HEIGHT/4);
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

    /**
     * resetowanie gry i ustawienie wszystkich parametrow na poczatkowe
     */
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

    /**
     * glowny game loop
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        moveSnake MoveSnake = new moveSnake();
        moveAI MoveAI = new moveAI();
        BugMove bugMove = new BugMove();
        if(running){
            MoveAI.start();
            MoveSnake.start();
            bugMove.start();
            try {

                MoveSnake.join();
                checkCollisions();
                MoveAI.join();
                checkCollisions();
                bugMove.join();
                checkCollisions();
            }
            catch(Exception exc){
                System.out.println(exc);
            }
            checkFruit();

        }
        repaint();
    }

    /**
     * nasluchuje klikniecia strzalek przez gracza
     */
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
