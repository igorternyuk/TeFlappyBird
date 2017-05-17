package teflappybird;

import java.awt.Point;
import java.util.ArrayList;

public interface ConstantModel {
    int getScore();
    int getBirdY();
    boolean isAnimation();
    ArrayList<Point> getColumns();
    String getGameStatus();
}
