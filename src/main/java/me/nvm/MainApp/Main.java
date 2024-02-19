package me.nvm.MainApp;

import me.nvm.Network.Network;
import me.nvm.Network.NetworkBuilder;
import me.nvm.game.Game;
@Deprecated
public class Main {
    //Do not use -> Use instead MainApp
    public static void main(String[] args) {

        Network network = new NetworkBuilder()
                .setInputLayerSize(4)
                .addReLULayer(4)
                .addReLULayer(4)
                .setOutputLayerSize(2)
                .build();

        System.out.println(network.toString());
        //MainApp.launch();
    }
}