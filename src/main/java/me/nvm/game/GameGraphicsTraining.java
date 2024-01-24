package me.nvm.game;

import me.nvm.MainApp.Resolution;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class GameGraphicsTraining extends JFrame implements GraphicsInterface {
    Map<Integer, Bird> birdMap;
    RenderingPanel makeShitCanvas;
    GameBackendAI gameBackendAI;
    Queue<PipePair> pipePairs;
    double scale;
    int frameWidth;
    int  frameHeight;

    public GameGraphicsTraining(GameBackendAI gameBackendAI, Resolution resolution){
        setWindow(resolution);

        this.gameBackendAI = gameBackendAI;
        birdMap = gameBackendAI.birdMap;
        pipePairs = gameBackendAI.pipePairs;

        makeShitCanvas = new RenderingPanel();
        add(makeShitCanvas);
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
                    int circleRadius = 25; // Adjust the radius as needed
                    g.fillOval((int) ((bird.coordinateX - circleRadius) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) ((circleRadius * 2) * scale), (int) ((circleRadius * 2) * scale));
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
