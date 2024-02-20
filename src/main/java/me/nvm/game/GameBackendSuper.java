package me.nvm.game;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import me.nvm.MainApp.Resolution;
import me.nvm.game.GameGraphicsTraining;
import me.nvm.game.GameState;
import me.nvm.game.gameobjects.PipePair;

import java.util.*;

import static me.nvm.MainApp.AuxilaryTools.getRandomNumber;

public abstract class GameBackendSuper extends Thread {
    GameState gameState;
    GraphicsInterface gameGraphics;

    Queue<PipePair> pipePairs = new LinkedList<>();

    protected final Resolution mainBackendResolution = Resolution.SD;

    private long lastTime; // Time in nanos of last rendered frame.

    //TODO automatické vyvažování FPS podle zrychlení hry
    private final double TARGET_FPS = 60; // How many times should the game "refresh" or "check" conditions per second
    long graphicalFPS = 60;
    private final double OPTIMAL_TIME = 1e9 / TARGET_FPS; // How much nanos for each game loop to sustain target FPS
    private int frames = 0; // How many gameloops/frames has been
    private long lastFPSTime; // Time in nanos of last rendered frame. Used in computations for FPS measuring.

    int minUpper = 30;
    int maxUpper = 400;

    int birdRadius = 25;
    int pipeWidth = 50;

    private double lastPipeLenght;

    private double speedMultiplier = 1;

    long lastRefreshTime = System.nanoTime();

    long refreshInterval = (long) (1e9 / graphicalFPS);

    long frameCountUI = 0;
    long lastFPSTimeUI = System.currentTimeMillis();
    StringProperty engineFPS = new SimpleStringProperty("NaN");
    StringProperty uiFPS = new SimpleStringProperty("NaN");

    public GameBackendSuper() {
        this.gameState = GameState.getInstance();
        System.out.println(getPriority());
        setPriority(7);
    }

    public void linkToGraphics(GraphicsInterface gameGraphics) {
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

        gameState.isGameRunning.set(true);

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

            if (gameState.isGameOver()) break;

            pointCounter();

            if (graphicalFPS == TARGET_FPS) {
                gameGraphics.refresh();
                frameCountUI++;
            } else if (now - lastRefreshTime >= refreshInterval) {//FIXME není optimální ztrácá fps jen tak kvůli podmínce
                frameCountUI++;
                gameGraphics.refresh();
                lastRefreshTime = now;
            }


            long loopTime = System.nanoTime() - now; // How much time did the game-loop take
            long sleepTime = (long) (OPTIMAL_TIME - loopTime); // How long should the program wait to get close to OPTIMAL_TIME
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000); // To milis
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            if (now - lastFPSTimeUI >= 1000000000) {
                int framesTbD = (int) frameCountUI;
                Platform.runLater(() -> {
                    uiFPS.set(String.valueOf(framesTbD));
                });
                frameCountUI = 0;
                lastFPSTimeUI = now;
            }

            frames++;

            if (now - lastFPSTime >= 1000000000) {

                int framesTbD = frames;
                Platform.runLater(() -> {
                    engineFPS.set(String.valueOf(framesTbD));
                });

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

    protected void updatePipes() {
        for (PipePair element : pipePairs) {
            element.move();
        }
    }

    protected void update() {
        updateBird();
        updatePipes();
    }

    ;

    protected void pipeLifecycle() {
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
    }

    ;

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getWidth() {
        return mainBackendResolution.getWidth();
    }

    public int getHeight() {
        return mainBackendResolution.getHeight();
    }
}
