package me.nvm.game;

import me.nvm.MainApp.Resolution;
import me.nvm.game.GameGraphicsTraining;
import me.nvm.game.GameState;
import me.nvm.game.gameobjects.PipePair;

import java.util.*;

import static me.nvm.MainApp.AuxilaryTools.getRandomNumber;

public abstract class GameBackendSuper extends Thread{
    GameState gameState;
    GraphicsInterface gameGraphics;

    Queue<PipePair> pipePairs = new LinkedList<>();

    protected final Resolution mainBackendResolution = Resolution.SD;

    private long lastTime; // Time in nanos of last rendered frame.
    private final double TARGET_FPS = 60; // How many times should the game "refresh" or "check" conditions per second
    private final double OPTIMAL_TIME = 1e9 / TARGET_FPS; // How much nanos for each game loop to sustain target FPS
    private int frames = 0; // How many gameloops/frames has been
    private long lastFPSTime; // Time in nanos of last rendered frame. Used in computations for FPS measuring.

    int minUpper = 30;
    int maxUpper = 400;

    int birdRadius = 25;
    int pipeWidth = 50;

    private double lastPipeLenght;

    private double speedMultiplier = 1;

    public GameBackendSuper() {
        this.gameState = GameState.getInstance();
    }

    public void linkToGraphics(GraphicsInterface gameGraphics){
        this.gameGraphics = gameGraphics;
    }

    @Override
    public void run() {
        gameLoop();
    }

    public void gameLoop() {
        gameGraphics.showWindow();

        lastTime = System.nanoTime();
        lastFPSTime = System.nanoTime();

        int a = getRandomNumber(minUpper, maxUpper);
        pipePairs.add(new PipePair(a, ((getHeight() - a) - gameState.getSizeOfHole()), getWidth()));


        while (!gameState.isGameOver()) {
            long now = System.nanoTime(); // Time when the curr loop started
            long updateTime = now - lastTime;

            lastTime = now;
            double delta = updateTime / 1e9;
            gameState.setDeltaTime(delta * speedMultiplier);

            handleInput();

            update();
            pipeLifecycle();

            checkCollisions();

            if(gameState.isGameOver()) break;

            pointCounter();

            gameGraphics.refresh();

            long loopTime = System.nanoTime() - now; // How much time did the game-loop take
            long sleepTime = (long) (OPTIMAL_TIME - loopTime); // How long should the program wait to get close to OPTIMAL_TIME
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000); // To milis
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frames++;
            //System.out.println(frames);
            if (now - lastFPSTime >= 1000000000) { // One second has passed
                //System.out.println("FPS: " + frames);
                frames = 0;
                lastFPSTime = now;
            }
        }
        System.out.println("Final score = " + gameState.getScore());

        gameGraphics.closeWindow();
    }

    protected abstract void pointCounter();
    protected abstract void checkCollisions();
    protected abstract void updateBird();
    protected abstract void handleInput();

    protected void updatePipes(){
        for (PipePair element : pipePairs) {
            element.move();
        }
    }
    protected void update(){
        updateBird();
        updatePipes();
    };
    protected void pipeLifecycle(){
        double lenghtNow = gameState.getLenght();

        if (lenghtNow - lastPipeLenght < -gameState.getSizeOfGaps()) {
            int a = getRandomNumber(100, 350);
            pipePairs.add(new PipePair(a, ((getHeight() - a) - gameState.getSizeOfHole()), getWidth()));
            lastPipeLenght = lenghtNow;
        }

        if (pipePairs.peek().position <= 0) {
            pipePairs.remove();
        }

        gameState.setLenght((lenghtNow + (gameState.getSpeed() * gameState.getDeltaTime())));
    };

    public void setSpeedMultiplier(double speedMultiplier){
        if (!this.isAlive()) this.speedMultiplier = speedMultiplier;
        else System.out.println("Cant change speed during game");
    }

    public int getWidth(){
        return mainBackendResolution.getWidth();
    }
    public int getHeight(){
        return mainBackendResolution.getHeight();
    }
}
