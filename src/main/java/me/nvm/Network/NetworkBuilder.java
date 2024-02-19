package me.nvm.Network;

import java.util.ArrayList;

public class NetworkBuilder {
    private ArrayList<LayerBluePrint> layerBluePrints = new ArrayList<>();

    public NetworkBuilder setInputLayerSize(int inputSize){
        layerBluePrints.add(new LayerBluePrint(inputSize, null));
        return this;
    }

    public NetworkBuilder setOutputLayerSize(int outputSize){
        addSigmoidLayer(outputSize);
        return this;
    }

    public NetworkBuilder addReLULayer(int layerSize){
        layerBluePrints.add(new LayerBluePrint(layerSize, new ReLu()));
        return this;
    }

    public NetworkBuilder addSigmoidLayer(int layerSize){
        layerBluePrints.add(new LayerBluePrint(layerSize, new SigmoidFun()));
        return this;
    }

    public NetworkBuilder addCustomLayer(int layerSize, ActivationFunction function){
        layerBluePrints.add(new LayerBluePrint(layerSize, function));
        return this;
    }


    public Network build(){
        return new Network(layerBluePrints);
    }
}


