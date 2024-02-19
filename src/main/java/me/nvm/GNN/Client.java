package me.nvm.GNN;

import me.nvm.MainApp.UniqueIDGenerator;
import me.nvm.Network.Network;
import me.nvm.Network.NetworkBuilder;


public class Client implements Comparable<Client> {
    public final int id;
    public double fitnessScore = 0;

    private GeneticInfo geneticInfo;

    public Network network;


    public double[] compute(double[] input){
        network.setInput(input);
        network.compute();

        return network.getOutput();
    }

    public int getInputSize(){
        return network.inputSize;
    }

    public Client(GeneticInfo geneticInfo) {
        this.id = UniqueIDGenerator.getRandomID();

        this.geneticInfo = geneticInfo;

        this.network = GenomProcessor.decodeGenom(this.geneticInfo);
    }

    public Client(int[] structure) {
        this.id = UniqueIDGenerator.getRandomID();




        NetworkBuilder builder = new NetworkBuilder()
                .setInputLayerSize(structure[0]);

        for (int i = 1; i < structure.length - 1; i++) {
            builder.addReLULayer(structure[i]);
        }

        this.network = builder.setOutputLayerSize(structure[structure.length - 1])
                .build();

        this.geneticInfo = GenomProcessor.encodeGenom(network);
    }

    public GeneticInfo getGenom() {
        return geneticInfo;
    }


    @Override
    public int compareTo(Client o) {
        return Double.compare(o.fitnessScore, this.fitnessScore);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", fitnessScore=" + fitnessScore +
                '}';
    }

    public int getId() {
        return id;
    }
}
