package me.nvm.MainApp;

import me.nvm.Network.Network;
import me.nvm.Network.NetworkBuilder;
import me.nvm.Network.NetworkVisualiser;
import me.nvm.game.Game;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class Main {
    //Do not use -> Use instead MainApp
    public static void main(String[] args) {
        Network network = new NetworkBuilder()
                .setInputLayerSize(4)
                .addReLULayer(3)
                .addReLULayer(4)
                .setOutputLayerSize(1)
                .build();

        network.setInput(new double[]{1.0,0.3,0.7,0.3});
        network.compute();

        JFrame jFrame = new JFrame();
        NetworkVisualiser networkVisualiser = new NetworkVisualiser(network);

        JPanel panel = new JPanel();

        panel.setBackground(new Color(29,29,29));
        panel.setSize(500,500);

        jFrame.setSize(500,500);

        panel.setLayout(new BorderLayout());

// Add the NetworkVisualiser panel to the container panel
        panel.add(networkVisualiser, BorderLayout.WEST);

        panel.add(networkVisualiser);


        jFrame.add(panel);

        jFrame.setVisible(true);



    }
}