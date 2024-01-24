package me.nvm.GNN;

import me.nvm.Network.Network;
import me.nvm.Network.Neuron;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenomProcessor {
    /*
    Structure of genom. First numOfBiases cells in array are reserved for bias information.
    And the rest (numOfWeights) cells are for weights information.
     */
    public static Network decodeGenom(GeneticInfo geneticInfo){

        int[] structure = geneticInfo.structure;
        double[] genom = geneticInfo.genom;

        Network network = new Network(structure);

        int numberOfBiases = numOfBiases(structure);
        int numberOfWeights = numOfWeights(structure);

        int lastReadBiasPosition = 0;
        int lastReadWeightPosition = numberOfBiases;

        for(int layer = 1; layer < network.numOfLayers; layer++){
            Neuron[] neurons = network.layers[layer].getNeurons();
            for (int neuron = 0;neuron < neurons.length; neuron++) {
                neurons[neuron].bias = genom[lastReadBiasPosition];
                lastReadBiasPosition++;

                double[] weights = neurons[neuron].inputWeights;
                for (int weight = 0;weight < weights.length; weight++) {
                    weights[weight] = genom[lastReadWeightPosition];
                    lastReadWeightPosition++;
                }
            }
        }
        return network;
    }

    public static GeneticInfo encodeGenom(Network network){
        int[] structure = network.getLayerSizes();
        int numberOfBiases = numOfBiases(structure);
        int numberOfWeights = numOfWeights(structure);
        int lengthenGenome = numberOfWeights + numberOfBiases;

        int lastWrittenBiasPosition = 0;
        int lastWrittenWeightPosition = numberOfBiases;
        double[] genom = new double[lengthenGenome];

        for(int layer = 1; layer < network.numOfLayers; layer++){
            Neuron[] neurons = network.layers[layer].getNeurons();
            for (Neuron neuron: neurons) {
                genom[lastWrittenBiasPosition] = neuron.bias;
                lastWrittenBiasPosition++;

                double[] weights = neuron.inputWeights;

                for (double weight: weights) {
                    genom[lastWrittenWeightPosition] = weight;
                    lastWrittenWeightPosition++;
                }
            }
        }
        return new GeneticInfo(structure, genom);
    }
//https://stackoverflow.com/questions/34958829/how-to-save-a-2d-array-into-a-text-file-with-bufferedwriter

    public static void saveGenom(GeneticInfo geneticInfo, String fileName){
        int[] structure = geneticInfo.structure;
        double[] genom = geneticInfo.genom;

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < structure.length; i++) {
            builder.append(structure[i] + "");
            if(i < structure.length - 1){
                builder.append(",");
            }
        }
        builder.append("\n");

        for(int i = 0; i < genom.length; i++) {
            builder.append(genom[i] + "");
            if(i < genom.length - 1){
                builder.append(",");
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName + ".txt"));
            writer.write(builder.toString());//save the string representation of the board
            writer.close();
            System.out.println("Writing successful");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GeneticInfo loadGenom(String fileName){
        GeneticInfo geneticInfo;
        double[] genom;
        int[] structure;

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName));

            String firstLine = reader.readLine();
            String[] firstLineStrArray = firstLine.split(",");

            structure = Arrays.stream(firstLineStrArray)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            String secondLine = reader.readLine();
            String[] secondLineStrArray = secondLine.split(",");

            genom = Arrays.stream(secondLineStrArray)
                    .mapToDouble(Double::parseDouble)
                    .toArray();

            reader.close();
            System.out.println("reading successful");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        geneticInfo = new GeneticInfo(structure,genom);
        return geneticInfo;
    }




    private static int numOfBiases(int[] structure){
        int sum = 0;
        for (int i = 1; i < structure.length; i++){
            sum += structure[i];
        }
        return sum;
    }
    private static int numOfWeights(int[] structure){
        int sum = 0;
        for (int i = 1; i < structure.length; i++){
            sum += structure[i-1] * structure[i];
        }
        return sum;
    }


}