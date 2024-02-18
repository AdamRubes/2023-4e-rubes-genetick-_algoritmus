package me.nvm.game;

import me.nvm.GNN.Client;
import me.nvm.game.GameBackendSuper;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import java.util.*;

import static me.nvm.MainApp.AuxilaryTools.normalizeValue;

public class GameBackendAI extends GameBackendSuper {
    HashMap <Integer, Client> clientMap;
    Map<Integer, Bird> birdMap = Collections.synchronizedMap(new HashMap<>());

    double lengthToNextPipe = (double) getWidth() /2;

    int indexOfNextPipe;

    public GameBackendAI(HashMap<Integer, Client> clientHashMap) {
        super();
        this.clientMap = clientHashMap;

        for (Map.Entry<Integer, Client> clientEntry : this.clientMap.entrySet()) {
            int id = clientEntry.getKey();
            Bird bird = new Bird((double) (getWidth() / 2), (double) getHeight() / 2);

            birdMap.put(id, bird);
        }
    }

    @Override
    protected void pointCounter() {
        double deltaX = 6666;
        LinkedList<PipePair> pipePairLinkedList = (LinkedList<PipePair>) pipePairs;

        for (int i = 0; i < pipePairLinkedList.size(); i++){
            double a = pipePairLinkedList.get(i).position - getWidth()/2;
            if(a >= 0){
                if(a < deltaX){
                    deltaX = a;
                    indexOfNextPipe = i;
                }
            }
        }
//        System.out.println("%%%%%%%%%%%");
//        System.out.println(birdMap.get(1).coordinateX);
//        System.out.println(pipePairLinkedList.get(indexOfNextPipe));
//        System.out.println(deltaX);

        if(Math.abs(lengthToNextPipe - deltaX) > gameState.getSizeOfGaps() * 0.75){
            gameState.incrementScore();
            //System.out.println("SCORE: " + gameState.getScore());
        }
        lengthToNextPipe = deltaX + pipeWidth; // do konce trubky

        for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
            clientMap.get(birdEntry.getKey()).fitnessScore = Math.abs(gameState.getLenght());
        }
    }

    @Override
    protected void checkCollisions() {
        int birdRadius = 25;
        int pipeWidth = 50;

        Iterator<Map.Entry<Integer, Bird>> iterator = birdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bird> birdEntry = iterator.next();
            Bird bird = birdEntry.getValue();

            if (bird.coordinateY < 0 || bird.coordinateY > getHeight()) {
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


        gameState.setNumOfLivingBirds(birdMap.size());
        if(gameState.getNumOfLivingBirds() <= 0) gameState.setGameOver(true);
    }


    @Override
    protected void updateBird() {
        for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
            birdEntry.getValue().update();
        }
    }

    @Override
    protected void handleInput() {
        //FIXME Zajistit pouze kladné vstpuny do NN
        // pak fixnout normalizaci ve vizualizeru
        for (Map.Entry<Integer, Client> clientEntry : clientMap.entrySet()) {
            if(birdMap.containsKey(clientEntry.getKey())){
                Client client = clientEntry.getValue();
                LinkedList<PipePair> linkedList = (LinkedList<PipePair>) pipePairs;

                double coordinateY = birdMap.get(clientEntry.getKey()).coordinateY;
                double deltaX = lengthToNextPipe;
                double upperPipe = linkedList.get(indexOfNextPipe).getUpperY();
                double lowerPipe = linkedList.get(indexOfNextPipe).getLowerY();

                double[] inputArr = new double[client.getInputSize()];

                double frstParam = normalizeValue(coordinateY, 0, getHeight());
                double secondParam = normalizeValue(deltaX, getWidth()/2, 0);
                double thirdParam = normalizeValue(upperPipe, minUpper, maxUpper);
                double fourthParam = normalizeValue(lowerPipe,((getHeight() - 400) - gameState.getSizeOfHole()),((getHeight() - 30) - gameState.getSizeOfHole()));

                inputArr[0] = frstParam;
                inputArr[1] = secondParam;
                inputArr[2] = thirdParam;
                inputArr[3] = fourthParam;

//                for (double param: inputArr){
//                    if (param < 0) System.out.println("Zaporny" + param);
//                }

                double[] output = client.compute(inputArr);
                //System.out.println(client.id + "{output1: " +output[1] + " output2: " +output[1] + "}");

               if (output[0] > output[1] && Math.abs(output[0] - output[1]) > 0.6) birdMap.get(clientEntry.getKey()).jump();

            }
        }
    }
}
