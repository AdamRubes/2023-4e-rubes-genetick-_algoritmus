package me.nvm.MainApp;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import me.nvm.GNN.Client;
import me.nvm.GNN.GeneticAlgorithm;
import me.nvm.game.Game;

import java.util.HashMap;

public class Training extends Thread{
    Game game;
    boolean stopTraining = false;
    GeneticAlgorithm geneticAlgorithm;
    HashMap<Integer, Client> clientHashMap;
    int sizeOfGeneration;
    int[] structure;
    int numOfElites;
    int numOfRuns;
    double mutationStrength;
    double mutationRate;
    int numOfElitesPrinted;

    DoubleProperty progress =  new SimpleDoubleProperty(0.0);

    public Training(int sizeOfGeneration, int[] structure, int numOfElites, int numOfRuns, double mutationStrength, double mutationRate, Game game, int numOfElitesPrinted) {
        this.sizeOfGeneration = sizeOfGeneration;
        this.structure = structure;
        this.numOfElites = numOfElites;
        this.numOfRuns = numOfRuns;
        this.mutationStrength = mutationStrength;
        this.mutationRate = mutationRate;

        this.game = game;

        this.numOfElitesPrinted = numOfElitesPrinted;

        geneticAlgorithm = new GeneticAlgorithm(numOfElites, mutationRate, mutationStrength);

        clientHashMap = new HashMap<>();

        for (int i = 0; i < sizeOfGeneration; i++) {
            Client client = new Client(structure);
            clientHashMap.put(client.id, client);
        }
    }

    @Override
    public void run() {
        stopTraining = false;
        for (int i = 0; i < numOfRuns; i++){


            System.out.println("Run:" + i + "/" + numOfRuns);

            game.startTrainingGame(clientHashMap, numOfElites);
            game.waitOnGame();


            Client[] clientsArray = geneticAlgorithm.evolve(clientHashMap.values().toArray(new Client[0]));
            geneticAlgorithm.saveGenomOfElites(numOfElitesPrinted);

            clientHashMap = new HashMap<>();
            for (Client client : clientsArray) {
                clientHashMap.put(client.id, client);
            }

            updateProgress(i);

            if(stopTraining) break;
        }
    }

    public void updateProgress(int currRun){
        double d1 = currRun +1;
        double d2 = numOfRuns;

        progress.setValue(d1/d2);
    }

    public void stopAfterCurrLoop(){
        stopTraining = true;
    }

    public void stopCurrRun(){
        game.stopGame();
    }

    public void stopTrainingNow(){
        stopAfterCurrLoop();
        game.stopGame();
    }
}
