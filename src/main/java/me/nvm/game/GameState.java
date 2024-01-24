package me.nvm.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameState {
    private static GameState instance = null;
    private int score;
    public IntegerProperty scoreProperty;
    public IntegerProperty numOfLivingBirdsProperty;
    private double lenght;
    private boolean isGameOver;
    private double deltaTime;
    private double gravity;
    private double speed;
    private double powerOfJump;
    private int sizeOfHole;
    private  int sizeOfGaps;
    private int numOfLivingBirds;


    private GameState() {
        this.isGameOver = false;
        this.score = 0;
        this.scoreProperty = new SimpleIntegerProperty(this, "value", 0);
        this.deltaTime = 0.0;
        this.gravity = 0.0;
        this.speed = 0.0;
        this.powerOfJump = 0.0;
        this.numOfLivingBirds = 0;
        this.numOfLivingBirdsProperty = new SimpleIntegerProperty(this,"birds", 0);
    }

    public  void reset(){
        this.score = 0;
        setProperty(scoreProperty, score);

        this.lenght = 0;
        this.isGameOver = false;
        this.deltaTime = 0.0;

        this.numOfLivingBirds = 0;
        setProperty(numOfLivingBirdsProperty, numOfLivingBirds);
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public static GameState getInstance(double gravity, double speed, double powerOfJump, int sizeOfHole, int sizeOfGaps) {
        GameState gameState = getInstance();
        gameState.setGravity(-gravity);
        gameState.setSpeed(-speed);
        gameState.setPowerOfJump(-powerOfJump);
        gameState.setSizeOfHole(sizeOfHole);
        gameState.setSizeOfGaps(sizeOfGaps);
        return gameState;
    }

    public double getPowerOfJump() {
        return powerOfJump;
    }

    public void setPowerOfJump(double powerOfJump) {
        this.powerOfJump = powerOfJump;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        setProperty(scoreProperty,score);
    }

    public void incrementScore(){
        score++;
        setProperty(scoreProperty,score);
    }

    public void setProperty(IntegerProperty property,int value){

        Platform.runLater(() -> {
            property.set(value);
        });


    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(double deltaTime) {
        this.deltaTime = deltaTime;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getLenght() {
        return lenght;
    }

    public void setLenght(double lenght) {
        this.lenght = lenght;
    }

    public int getSizeOfHole() {
        return sizeOfHole;
    }

    public void setSizeOfHole(int sizeOfHole) {
        this.sizeOfHole = sizeOfHole;
    }

    public int getSizeOfGaps() {
        return sizeOfGaps;
    }

    public void setSizeOfGaps(int sizeOfGaps) {
        this.sizeOfGaps = sizeOfGaps;
    }

    public int getNumOfLivingBirds() {
        return numOfLivingBirds;
    }

    public void setNumOfLivingBirds(int numOfLivingBirds) {
        this.numOfLivingBirds = numOfLivingBirds;
        setProperty(numOfLivingBirdsProperty,numOfLivingBirds);
    }

    @Override
    public String toString() {
        return "GameState{" +
                "score=" + score +
                ", lenght=" + lenght +
                ", isGameOver=" + isGameOver +
                ", deltaTime=" + deltaTime +
                ", gravity=" + gravity +
                ", speed=" + speed +
                ", powerOfJump=" + powerOfJump +
                ", sizeOfHole=" + sizeOfHole +
                ", sizeOfGaps=" + sizeOfGaps +
                '}';
    }
}
