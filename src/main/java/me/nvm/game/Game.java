package me.nvm.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import me.nvm.GNN.Client;
import me.nvm.MainApp.Resolution;
import me.nvm.Network.NetworkVisualiser;

import java.util.HashMap;
import java.util.Set;

public class Game {
    GameState gameState;
    GameBackendSuper mainBackend;
    GraphicsInterface mainGraphics;
    double speedMultiplier;
    boolean isHeadless;
    Resolution resolution;

    public StringProperty engineFPS = new SimpleStringProperty("NaN");
    public StringProperty uiFPS = new SimpleStringProperty("NaN");

    public Game(double gravity, double speed, double powerOfJump, int sizeOfHole, int sizeOfGaps, boolean isHeadless, double speedMultiplier, Resolution resolution) {
        this.isHeadless = isHeadless;
        this.speedMultiplier = speedMultiplier;
        this.resolution = resolution;

        this.gameState = GameState.getInstance(gravity, speed, powerOfJump, sizeOfHole, sizeOfGaps);
    }

    public StringProperty getEngineFPS(){
        return mainBackend.engineFPS;
    }

    public StringProperty getUIFPS(){
        return mainBackend.uiFPS;
    }

    public void startPlayerGameReworked(){
        restartReworked();
        mainBackend = new GameBackendPlayer();
        changeSpeedOfGame(speedMultiplier);
        mainGraphics = new GameGraphics((GameBackendPlayer) mainBackend, resolution);
        mainBackend.linkToGraphics(mainGraphics);

        Platform.runLater(() -> {
                    engineFPS.bind(getEngineFPS());
                    uiFPS.bind(getUIFPS());
                });
        mainBackend.start();
    }

    public void startTrainingGame(HashMap<Integer, Client> clientHashMap, int numOfElites) {
        restartReworked();
        System.out.println(speedMultiplier);
        mainBackend = new GameBackendAI(clientHashMap,numOfElites);

        changeSpeedOfGame(speedMultiplier);
        System.out.println(mainBackend.getSpeedMultiplier());
        GameGraphicsTraining gameGraphicsTraining = new GameGraphicsTraining((GameBackendAI) mainBackend, resolution);

        mainGraphics = gameGraphicsTraining;
        mainBackend.linkToGraphics(mainGraphics);
        Platform.runLater(() -> {
            engineFPS.bind(getEngineFPS());
            uiFPS.bind(getUIFPS());
        });


        mainBackend.start();
    }


    public void restartReworked(){
        this.gameState.reset();
    }

    public void stopGame() {
        gameState.setGameOver(true);
    }

    public void waitOnGame(){
        try {
            mainBackend.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeResolution(Resolution resolution){
        this.resolution = resolution;
        mainGraphics.setResolution(resolution);
    }

    public void changeSpeedOfGame(double speedMultiplier){
        this.speedMultiplier = speedMultiplier;
        mainBackend.setSpeedMultiplier(this.speedMultiplier);
    }


}
