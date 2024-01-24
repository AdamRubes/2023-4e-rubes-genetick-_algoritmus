package me.nvm.game;

import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

public class GameBackendPlayer extends GameBackendSuper{
    boolean userJumped = false;
    Bird bird;

    double lengthToNextPipe = (double) getWidth() / 2;

    public GameBackendPlayer() {
        super();
        bird = new Bird((double) getWidth() / 2, (double) getHeight() / 2);
    }

    @Override
    protected void pointCounter() {
        double deltaX = 6666;
        for (PipePair pipePair: pipePairs) {
            double a = pipePair.position - bird.coordinateX;
            if(a >= 0){
                if(a < deltaX){
                    deltaX = a;
                }
            }
        }
        if(Math.abs(lengthToNextPipe - deltaX) > gameState.getSizeOfGaps() * 0.75){
            gameState.incrementScore();
            System.out.println("SCORE======" + gameState.getScore());
        }
        lengthToNextPipe = deltaX;
    }

    @Override
    protected void checkCollisions() {
        for (PipePair element : pipePairs) {
            if (Math.abs(bird.coordinateX - element.position) < birdRadius + pipeWidth / 2) {
                int upperYBoundry = element.getUpperY();
                int lowerYBoundry = element.getUpperY() + gameState.getSizeOfHole();

                double birdY = bird.coordinateY;
                if (birdY <= upperYBoundry + birdRadius || birdY >= lowerYBoundry - birdRadius) {
                    gameState.setGameOver(true);
                }
            }
        }
    }

    @Override
    protected void updateBird() {
        bird.update();
    }

    @Override
    protected void handleInput() {
        if (userJumped) {
            bird.jump();
            userJumped = false;
        }
    }
}
