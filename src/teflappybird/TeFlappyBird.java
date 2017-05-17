package teflappybird;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeFlappyBird {
    public void go() throws IOException{
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(model, controller);
        model.addView(view);
    }
    public static void main(String[] args) {
        TeFlappyBird fb = new TeFlappyBird();
        try {
            fb.go();
        } catch (IOException ex) {
            Logger.getLogger(TeFlappyBird.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
