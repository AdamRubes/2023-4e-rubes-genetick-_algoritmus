package me.nvm.DeprecatedCode;


import me.nvm.MainApp.AuxilaryTools;
import me.nvm.game.GameGraphics;
import me.nvm.game.GameState;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

import static me.nvm.MainApp.AuxilaryTools.getRandomNumber;

@Deprecated
public class GameBackend extends Thread {
    GameState gameState;
    GameGraphics graphics;
    Bird bird;
    Queue<PipePair> pipePairs = new LinkedList<>();

    private long lastTime; // Time in nanos of last rendered frame.
    private final double TARGET_FPS = 60; // How many times should the game "refresh" or "check" conditions per second
    private final double OPTIMAL_TIME = 1e9 / TARGET_FPS; // How much nanos for each game loop to sustain target FPS
    private int frames = 0; // How many gameloops/frames has been
    private long lastFPSTime; // Time in nanos of last rendered frame. Used in computations for FPS measuring.
    private final int width = 1280;
    private final int height = 720;
    private boolean userJumped = false;
    private double lastPipeLenght;
    double lengthToNextPipe = (double) width /2;



    public GameBackend() {
        this.gameState = GameState.getInstance();
        bird = new Bird((double) width / 2, (double) height / 2);
        //graphics = new GameGraphics(this);
        graphics.setVisible(true);
    }

    @Override
    public void run() {
        gameLoop();
    }

    public void gameLoop() {
        JOptionPane.showConfirmDialog(null, "Are you ready?", "Game Start Confirmation", JOptionPane.YES_NO_OPTION);

        lastTime = System.nanoTime();
        lastFPSTime = System.nanoTime();

        int a = getRandomNumber(30,400);
        pipePairs.add(new PipePair(a, ((height - a) - gameState.getSizeOfHole()), width));


        while (!gameState.isGameOver()) {
            long now = System.nanoTime(); // Time when the curr loop started
            long updateTime = now - lastTime;
            lastTime = now;

            double delta = updateTime / 1e9;

            gameState.setDeltaTime(delta);

            checkCollisions();
            if (gameState.isGameOver()) {// pro grafiku lepší break okam   žitě než nechat loop
                break;
            }

            //ToDo Handle user input
            handleInput();

            //ToDo Update game state
            update();
            pointCounter();

            //ToDo Render the game
            graphics.refresh();

            long loopTime = System.nanoTime() - now; // How much time did the game-loop take
            long sleepTime = (long) (OPTIMAL_TIME - loopTime); // How long should the program wait to get close to OPTIMAL_TIME
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000); // To milis
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Calculate FPS
            frames++;
            //System.out.println(frames);
            if (now - lastFPSTime >= 1000000000) { // One second has passed
                //System.out.println("FPS: " + frames);
                frames = 0;
                lastFPSTime = now;
            }
        }
        try {
            Thread.sleep(300); // To milis
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Final score = " + gameState.getScore());
        graphics.dispose();
    }

    public void update() {
        bird.update();
        for (PipePair element : pipePairs) {
            element.move();
        }
        pipeLifecycle();
    }

    public void pipeLifecycle() {
        double lenghtNow = gameState.getLenght();

        if (lenghtNow - lastPipeLenght < -gameState.getSizeOfGaps()) {
            int a = getRandomNumber(100, 350);
            pipePairs.add(new PipePair(a, ((height - a) - gameState.getSizeOfHole()), width));
            lastPipeLenght = lenghtNow;
        }

        if (pipePairs.peek().position <= 0) {
            pipePairs.remove();
        }

        gameState.setLenght((lenghtNow + (gameState.getSpeed() * gameState.getDeltaTime())));
    }

    public void checkCollisions() {
        int birdRadius = 25;
        int pipeWidth = 50;

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




    public void pointCounter(){
        double deltaX = 6666;
        for (PipePair pipePair: pipePairs) {
            double a = pipePair.position - bird.coordinateX;
            if(a >= 0){
                if(a < deltaX){
                    deltaX = a;
                }
            }
           // deltaX = pipePair.position - bird.coordinateX ;
        }
        if(Math.abs(lengthToNextPipe - deltaX) > gameState.getSizeOfGaps() * 0.75){
            gameState.incrementScore();
            System.out.println("SCORE======" + gameState.getScore());
        }
        lengthToNextPipe = deltaX;
    }

    public void handleInput() {
        if (userJumped) {
            bird.jump();
            userJumped = false;
        }
    }

    public void setUserJumped(boolean userJumped) {
        this.userJumped = userJumped;
    }

    public Bird getBird() {
        return bird;
    }

    public Queue<PipePair> getPipePairs() {
        return pipePairs;
    }


}