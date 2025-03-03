import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection (char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVilocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVilocity();
                }
            }
        }
        void updateVilocity(){
            if (this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = - titleSize/4; 
            }
            else if (this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = titleSize/4; 
            }
            else if (this.direction == 'L'){
                this.velocityX = - titleSize/4; 
                this.velocityY = 0;
            }
            else if (this.direction == 'R'){
                this.velocityX = titleSize/4; 
                this.velocityY = 0;
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }
    }
    private int rowCount = 21;
    private int columnCount = 19;
    private int titleSize = 32;
    private int boardWWidth = columnCount * titleSize;
    private int boardHeight = rowCount * titleSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;

    //X = wall, O = skip, P = pac man, ' ' = food <Space characters>
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X   XO",
        "XXXX X XXrXX X XXXX",
        "O       b o       O",
        "XXXX X X X X X XXXX",
        "OOOX X   p   X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
    
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    //Need proceed the loop we need timer
    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    //Tracking score attr
    int score = 0;
    int lives = 3;
    boolean gameOver = false;


    PacMan (){
        setPreferredSize(new Dimension(boardWWidth, boardHeight));
        setBackground(Color.BLACK);

        //Move key
        addKeyListener(this);
        setFocusable(true);
 
        //Load Img
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();

        loadMap();

        for (Block ghost : ghosts){
            char newDirections = directions[random.nextInt(4)];
            ghost.updateDirection(newDirections);
        }

        
        gameLoop = new Timer(50, this); //1000/50 = 20fps
        gameLoop.start();
    }

    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++ ) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                
                int x = c*titleSize;
                int y = r*titleSize;

                if (tileMapChar == 'X') { //Block wall
                    Block wall = new Block(wallImage, x, y, titleSize, titleSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') { //Block blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //Block red ghost
                    Block ghost = new Block(redGhostImage, x, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') { //Block orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') { //Block pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //Block pacman
                    pacman = new Block(pacmanRightImage, x, y, titleSize, titleSize);
                }
                else if (tileMapChar == ' ') { //Block food
                    Block food = new Block(null, x  +14, y + 14, 4, 4);
                    foods.add(food);
                }

            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //g.fillRect(pacman.x, pacman.y, pacman.width, pacman.height);
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        
        for(Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.LIGHT_GRAY);
        for(Block food : foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        
        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), titleSize/2, titleSize/2);
        }
        else {
            g.drawString("X" + String.valueOf(lives) + " Score: " + String.valueOf(score), titleSize/2, titleSize/2);
        }
    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //Check wall collision
        for (Block wall : walls) {
            if (collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //Check ghost collision
        for (Block ghost: ghosts){
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0){
                    gameOver = true;
                    return;
                }
                resetPosition();
            }

            if (ghost.y == titleSize*9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');                
            }
            if (ghost.y == titleSize*9 && ghost.direction != 'R' && ghost.direction != 'L'){
                ghost.updateDirection(directions[random.nextInt(4)]);
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //Food collision
        Block foodEaten = null;
        for (Block food : foods){
            if (collision(pacman, food)){
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten); //Hashset

        if (foods.isEmpty()){
            loadMap();
            resetPosition();
        }

    }

    public boolean collision (Block a, Block b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPosition(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver){
            loadMap();
            resetPosition();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }

        if (pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }
    }


}
