package me.nvm.game;

import me.nvm.MainApp.Resolution;
import me.nvm.Network.Network;
import me.nvm.Network.NetworkVisualiser;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class GameGraphicsTraining extends JFrame implements GraphicsInterface {
    Map<Integer, Bird> birdMap;
    RenderingPanel makeShitCanvas;
    GameBackendAI gameBackendAI;
    Queue<PipePair> pipePairs;
    double scale;
    int frameWidth;
    int  frameHeight;
    JButton closeButton;
    GameState gameState;

    List<NetworkVisualiser> networkVisualisers = new ArrayList<>();

    public GameGraphicsTraining(GameBackendAI gameBackendAI, Resolution resolution){
        setWindow(resolution);

        gameState = GameState.getInstance();

        closeButton = new JButton("Stop game");
        closeButton.setBounds(10, 10, 100, 30);


        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameState.setGameOver(true);
            }
        });

        this.gameBackendAI = gameBackendAI;
        birdMap = gameBackendAI.birdMap;
        pipePairs = gameBackendAI.pipePairs;

        makeShitCanvas = new RenderingPanel();


        makeShitCanvas.setLayout(null);
        makeShitCanvas.add(closeButton);

//        System.out.println("AHOJ");
//        System.out.println(networkVisualisers);
//        for (NetworkVisualiser visualiser : networkVisualisers) {
//            System.out.println(visualiser.calculateWidth()+  " + " +visualiser.calculateHeight() );
//
//        }


        add(makeShitCanvas);
    }

    public void registerVisualiser(NetworkVisualiser visualiser){
        visualiser.setBounds(10, 15, visualiser.calculateWidth(), visualiser.calculateHeight());
        makeShitCanvas.add(visualiser);
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }
    @Override
    public void refresh(){
        makeShitCanvas.repaint();
    }

    @Override
    public void closeWindow() {
        dispose();
    }

    @Override
    public void setResolution(Resolution resolution) {
        setWindow(resolution);
    }

    private void setWindow(Resolution resolution){
        this.scale = resolution.scaleFromSD;
        this.frameHeight = resolution.getHeight();
        this.frameWidth = resolution.getWidth();

        setTitle("Flapy Bird");
        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        try {
            setUndecorated(true);
        }catch (Exception e){}
    }


    private class RenderingPanel extends JPanel {
        int birdWidth = 50;
        int birdHeight = 50;
        int pipeWidth = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

            synchronized (birdMap) {
                for (Map.Entry<Integer, Bird> birdEntry : birdMap.entrySet()) {
                    Bird bird = birdEntry.getValue();

                    g.setColor(Color.RED);
                    g.fillRect((int) ((bird.coordinateX - (birdWidth / 2)) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) (birdWidth * scale), (int) (birdHeight * scale));

                    g.setColor(Color.BLUE);
                    int circleRadius = 25;
                    g.fillOval((int) ((bird.coordinateX - circleRadius) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) ((circleRadius * 2) * scale), (int) ((circleRadius * 2) * scale));

                    g.setColor(Color.BLACK);
                    String keyText = String.valueOf(birdEntry.getKey());
                    int textX = (int) ((bird.coordinateX - (birdWidth / 2)) * scale) + 5;
                    int textY = (int) ((bird.coordinateY - (birdHeight / 2)) * scale) + 15;
                    g.drawString(keyText, textX, textY);
                }
            }

            synchronized (pipePairs) {
                g.setColor(Color.GREEN);
                for (PipePair element : pipePairs) {
                    int upperPipeHeight = (int) (element.getUpperY() * scale);
                    int lowerPipeHeight = (int) (element.getLowerY() * scale);
                    g.fillRect((int) ((element.position - pipeWidth / 2) * scale), 0, (int) (pipeWidth * scale), upperPipeHeight);
                    g.fillRect((int) ((element.position - pipeWidth / 2) * scale), frameHeight - lowerPipeHeight, (int) (pipeWidth * scale), lowerPipeHeight);
                }
            }
        }
    }
}
