package me.nvm.game.gameobjects;

public class Pipe extends GameObject{
    boolean isUp;
    public Pipe(boolean isUp,int coordinateX, int coordinateY) {
        super(coordinateX, coordinateY);
        this.isUp = isUp;
    }

    public void movePipe(){
        coordinateX = coordinateX + (gameState.getSpeed() * gameState.getDeltaTime());
    }
}
