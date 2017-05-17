package teflappybird;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class Model implements ConstantModel, ActionListener {
    private final int FIELD_LEFT = 0, FIELD_TOP = 0;
    private final int FIELD_RIGHT = Constants.WINDOW_WIDTH;
    private final int FIELD_BOTTOM = Constants.WINDOW_HEIGHT - Constants.GROUND_THICKNESS;
    private final ArrayList<ViewUpdater> views = new ArrayList<>();
    private GameStatus gameStatus = GameStatus.INITIAL;
    private final Bird bird;
    private final ArrayList<Column> columns = new ArrayList<>();
    private int score = 0;
    private final Random rand = new Random();
    private int tick = 0, tick2 = 0;
    private boolean doAnimation = false;
    private final int SCORE_INCREMENT = 5;
    private boolean isScoreIncremented = false;

    Timer timer;
    
    public Model() {
        this.timer = new Timer(Constants.TIMER_DELAY, this);
        this.bird = new Bird(Constants.BIRD_X, Constants.BIRD_Y, Constants.BIRD_WIDTH, 
                             Constants.BIRD_HEIGHT);
        addColumn();
        timer.start();
    }

    public void prepareNewGame(){
        score = 0;
        bird.reset();
        columns.clear();
        addColumn();
        gameStatus = GameStatus.PLAY;
        doAnimation = true;
    }
    
    public void initiateGame() {
        if (gameStatus == GameStatus.INITIAL) {
            gameStatus = GameStatus.PLAY;
            timer.start();
            doAnimation = true;
        }
    }

    public void jumpBird(boolean isStrongJump){
        bird.jump(isStrongJump);
        notifyAllListeners();
    }
    
    public void dropBird(boolean isStrongFalling){
        bird.drop(isStrongFalling);
    }
    
    public void togglePause(){
        if(gameStatus == GameStatus.PLAY){
            gameStatus = GameStatus.PAUSE;
            doAnimation = false;
        } else if(gameStatus == GameStatus.PAUSE) {
           gameStatus = GameStatus.PLAY;
           doAnimation = true;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //MAIN LOOP OF THE GAME
        if (gameStatus == GameStatus.PLAY) {
            ++tick;
            ++tick2;
            if (tick % 10 == 0) {
                addColumn();
                //System.out.println("Adding new column");
                tick = 0;
            }
            if(tick2 % 50 == 0){
                bird.fall();
            }

            for (Column column : columns) {
                column.moveLeft();
            }
            removeColumns();
            checkCollisions();
        }
        //System.out.println("Total columns = " + columns.size());
        notifyAllListeners();
    }

    public void addView(ViewUpdater v) {
        views.add(v);
    }

    public void removeView(ViewUpdater v) {
        views.remove(v);
    }

    public void notifyAllListeners() {
        for (ViewUpdater v : views) {
            v.updateView();
        }
    }
    
    @Override
    public int getScore(){
        return score;
    }
    
    @Override
    public int getBirdY() {
        return bird.rect.y;
    }

    @Override
    public String getGameStatus() {
        return gameStatus.getDescription();
    }

    @Override
    public ArrayList<Point> getColumns() {
        ArrayList<Point> result = new ArrayList<>();
        for(Column column : columns){
            if(column.getPositionX() + Constants.COLUMNS_WIDTH > FIELD_LEFT && 
               column.getPositionX() < FIELD_RIGHT){
               result.add(new Point(column.getPositionX(), column.getGapY()));
            }
        }
        return result;
    }

    @Override
    public boolean isAnimation() {
        return doAnimation;
    }

    private enum GameStatus {
        INITIAL("Press ENTER to start"),
        PLAY(""),
        PAUSE("GAME PAUSED"),
        GAME_OVER("GAME OVER");
        private final String description;

        public String getDescription() {
            return description;
        }

        private GameStatus(String description) {
            this.description = description;
        }
    }

    private class Bird {

        private Rectangle rect;
        private final int gravity;
        private int fallingSpeed;

        public Bird(int x, int y, int width, int height) {
            rect = new Rectangle(x, y, width, height);
            this.fallingSpeed = Constants.BIRD_FALLING_SPEED;
            this.gravity = Constants.GRAVITY;
        }
        
        public int getY(){
            return rect.y;
        }
        
        public boolean isTouchGround(){
            return rect.y + rect.height > FIELD_BOTTOM;
        }
        
        public void jump(boolean isStrongJump) {
            if (gameStatus == GameStatus.PLAY) {
                if (rect.y > FIELD_TOP) {
                    rect.y -= isStrongJump ? Constants.BIRD_JUMP_SPEED2 : Constants.BIRD_JUMP_SPEED1;
                } else {
                    rect.y = FIELD_TOP;
                }
            }
        }
        
        public void drop(boolean isStrongFalling) {
            if (gameStatus == GameStatus.PLAY) {
                if (isTouchGround()) {
                    rect.y = FIELD_BOTTOM - rect.height;
                    gameStatus = GameStatus.GAME_OVER;
                    doAnimation = false;
                } else {
                    rect.y += isStrongFalling ? Constants.BIRD_JUMP_SPEED2 : Constants.BIRD_JUMP_SPEED1;
                }
            }
        }

        public void fall() {
            if (gameStatus == GameStatus.PLAY) {
                if (isTouchGround()) {
                    rect.y = FIELD_BOTTOM - rect.height;
                    gameStatus = GameStatus.GAME_OVER;
                    doAnimation = false;
                } else {
                    rect.y += fallingSpeed;
                }
            }
        }
        
        public void reset(){
            rect.x = Constants.BIRD_X;
            rect.y = Constants.BIRD_Y;
            this.fallingSpeed = Constants.BIRD_FALLING_SPEED;
        }
    }
    
    private class Column{
        private int positionX;
        final int speedX;
        private final int gapY;
        public Column(){
            positionX = (columns.size() > 0) ? columns.get(columns.size() - 1).getPositionX() + Constants.COLUMNS_WIDTH + Constants.SPAN : FIELD_RIGHT;
            speedX = Constants.COLUMN_SPEED;
            gapY = 50 + rand.nextInt(260);
        }

        public int getPositionX() {
            return positionX;
        }
        
        public int getGapY(){
           return gapY;    
        }
        
        public void moveLeft(){
            positionX -= speedX;
        }
    }
    
    private void addColumn(){
        columns.add(new Column());
    }
    
    private void removeColumns(){
        for(int i = 0; i < columns.size(); ++i){
            if(columns.get(i).getPositionX() < FIELD_LEFT - Constants.COLUMNS_WIDTH){
                Column c = columns.get(i);
                columns.remove(c);
            }
        }
    }
    
    private boolean checkCollisions(){
        boolean result = false;
        for(Column column: columns){
            if(Constants.BIRD_X + Constants.BIRD_WIDTH > column.getPositionX() &&
               Constants.BIRD_X < column.getPositionX() + Constants.COLUMNS_WIDTH){
               //Есть коллизия, проверяем попадает ли птичка в зазор между столбами
               int delta = bird.getY() - column.getGapY();
               if(delta > 0 && delta < Constants.SPACE - Constants.BIRD_HEIGHT){
                   score += !isScoreIncremented ? SCORE_INCREMENT : 0;
                   isScoreIncremented = true;
                   break;
               } else {
                   gameStatus = GameStatus.GAME_OVER;
                   doAnimation = false;
                   break;
               }
            } else {
                isScoreIncremented = false;
            }
        }
        return result;
    }
}
