package me.nvm.DeprecatedCode;

import me.nvm.MainApp.AuxilaryTools;
import me.nvm.GNN.Client;
import me.nvm.game.GameGraphicsTraining;
import me.nvm.game.GameState;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import java.util.*;

import static me.nvm.MainApp.AuxilaryTools.getRandomNumber;
import static me.nvm.MainApp.AuxilaryTools.normalizeValue;

@Deprecated
public class GameBackendTraining extends Thread {

    GameState gameState;
    GameGraphicsTraining gameGraphicsTraining;

    Queue<PipePair> pipePairs = new LinkedList<>();
    HashMap <Integer, Client> clientMap;
    Map<Integer,Bird> birdMap = Collections.synchronizedMap(new HashMap<>());

    public boolean headless;
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
    int indexOfNextPipe;
    int minUpper = 30;
    int maxUpper = 400;


    public GameBackendTraining(HashMap <Integer, Client> clientMap, boolean headless) {
        gameState = GameState.getInstance();
        this.headless = headless;

        this.clientMap = clientMap;

        for (Map.Entry<Integer, Client> clientEntry : this.clientMap.entrySet()) {
            int id = clientEntry.getKey();
            Bird bird = new Bird((double) (width / 2), (double) height / 2);

            birdMap.put(id, bird);
        }
        System.out.println(headless);

        if(!headless){
            //gameGraphicsTraining = new GameGraphicsTraining(this);
            gameGraphicsTraining.setVisible(true);
        }
    }
    @Override
    public void run() {
        gameLoop();
    }

    public void gameLoop() {
        lastTime = System.nanoTime();
        lastFPSTime = System.nanoTime();

        int a = getRandomNumber(minUpper, maxUpper);
        pipePairs.add(new PipePair(a, ((height - a) - gameState.getSizeOfHole()), width));

        while (!gameState.isGameOver()) {
            long now = System.nanoTime(); // Time when the curr loop started
            long updateTime = now - lastTime;

            lastTime = now;

            double delta = updateTime / 1e9;
            gameState.setDeltaTime(delta);



            handleInput();




            update();
            pipeLifecycle();

            checkCollisions();
            checkLivingBirds();

            if(gameState.isGameOver()) break;


            pointCounter();
            giveScore();



            //ToDo Render the game
            if(!headless) {
                gameGraphicsTraining.refresh();// here is called the grafics
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
            // Calculate FPS
            frames++;
            //System.out.println(frames);
            if (now - lastFPSTime >= 1000000000) { // One second has passed
                System.out.println("FPS: " + frames);
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
        if(!headless){
            gameGraphicsTraining.dispose();
        }

    }

    public void checkLivingBirds(){
        gameState.setNumOfLivingBirds(birdMap.size());
        if(gameState.getNumOfLivingBirds() <= 0) gameState.setGameOver(true);
    }
    
    public void update() {
        for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
            birdEntry.getValue().update();
        }

        for (PipePair element : pipePairs) {
            element.move();
        }
    }

    public void giveScore(){
        //System.out.println("vzdalenost" + Math.abs(gameState.getLenght()));
        for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
            clientMap.get(birdEntry.getKey()).fitnessScore = Math.abs(gameState.getLenght());
        }
    }

    public void pipeLifecycle(){
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

        Iterator<Map.Entry<Integer, Bird>> iterator = birdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bird> birdEntry = iterator.next();
            Bird bird = birdEntry.getValue();

            if (bird.coordinateY < 0 || bird.coordinateY > height) {
                iterator.remove();
                //System.out.println("Bird id:" + birdEntry.getKey() + " died");
            } else {
                for (PipePair element : pipePairs) {
                    if (Math.abs(bird.coordinateX - element.position) < birdRadius + pipeWidth / 2) {
                        int upperYBoundry = element.getUpperY();
                        int lowerYBoundry = element.getUpperY() + gameState.getSizeOfHole();

                        double birdY = bird.coordinateY;
                        if (birdY <= upperYBoundry + birdRadius || birdY >= lowerYBoundry - birdRadius) {
                            iterator.remove();
                            //System.out.println("Bird id:" + birdEntry.getKey() + " died");
                        }
                    }
                }
            }
        }
    }
    public void pointCounter(){
        double deltaX = 6666;
        LinkedList<PipePair> pipePairLinkedList = (LinkedList<PipePair>) pipePairs;

        for (int i = 0; i < pipePairLinkedList.size(); i++){
            double a = pipePairLinkedList.get(i).position - width/2;
            if(a >= 0){
                if(a < deltaX){
                    deltaX = a;
                    indexOfNextPipe = i;
                }
            }
            // deltaX = pipePair.position - bird.coordinateX;
        }
        if(Math.abs(lengthToNextPipe - deltaX) > gameState.getSizeOfGaps() * 0.75){
            gameState.incrementScore();
            System.out.println("SCORE: " + gameState.getScore());
        }
        lengthToNextPipe = deltaX;
    }

    public void handleInput(){
        for (Map.Entry<Integer, Client> clientEntry : clientMap.entrySet()) {
            if(birdMap.containsKey(clientEntry.getKey())){
                Client client = clientEntry.getValue();
                LinkedList<PipePair> linkedList = (LinkedList<PipePair>) pipePairs;

                double coordinateY = birdMap.get(clientEntry.getKey()).coordinateY;
                double deltaX = lengthToNextPipe;
                double upperPipe = linkedList.get(indexOfNextPipe).getUpperY();
                double lowerPipe = linkedList.get(indexOfNextPipe).getLowerY();

                double[] inputArr = new double[client.getInputSize()];

                double frstParam = normalizeValue(coordinateY, 0, height);
                double secondParam = normalizeValue(deltaX, width/2, 0);
                double thirdParam = normalizeValue(upperPipe, minUpper, maxUpper);
                double fourthParam = normalizeValue(lowerPipe,((height - 400) - gameState.getSizeOfHole()),((height - 30) - gameState.getSizeOfHole()));

                inputArr[0] = frstParam;
                inputArr[1] = secondParam;
                inputArr[2] = thirdParam;
                inputArr[3] = fourthParam;

                double[] output = client.compute(inputArr);

                if (output[0] > output[1]) birdMap.get(clientEntry.getKey()).jump();
            }
            }
    }

    public Queue<PipePair> getPipePairs() {
        return pipePairs;
    }

    public Map<Integer, Bird> getBirds(){
        return birdMap;
    }

}
