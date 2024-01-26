package me.nvm.game.gameobjects;

public class Bird extends GameObject{
    public double velocity = 0;

    public Bird(double coordinateX, double coordinateY) {
        super(coordinateX, coordinateY);
    }

    public void jump(){
            velocity = gameState.getPowerOfJump();
    }

    public void update(){
            velocity = velocity - (gameState.getGravity() * gameState.getDeltaTime() / 2);
            coordinateY = coordinateY + (velocity * gameState.getDeltaTime());
            velocity = velocity - (gameState.getGravity() * gameState.getDeltaTime() / 2);
    }


    @Override
    public String toString() {
        return "Bird{" +
                "coordinateX=" + coordinateX +
                '}';
    }
}