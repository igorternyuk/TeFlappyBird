package teflappybird;

public class Controller {
    Model model;
    
    public Controller(Model model){
        this.model = model;
    }
    
    public void newGame(){
        model.prepareNewGame();
    }
    
    public void initiate(){
        model.initiateGame();
    }
    public void pause(){
        model.togglePause();
    }
    
    public void jump(boolean isStrongJump){
        model.jumpBird(isStrongJump);
    }
    
    public void drop(boolean isStrongFalling){
        model.dropBird(isStrongFalling);
    }
}
