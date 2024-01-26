package me.nvm.game;

import me.nvm.MainApp.Resolution;
import me.nvm.game.gameobjects.Bird;
import me.nvm.game.gameobjects.PipePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;

public class GameGraphics extends JFrame implements GraphicsInterface {
    Bird bird;
    RenderingPanel makeShitCanvas;
    GameBackendPlayer gameBackend;

    GameState gameState;
    Queue<PipePair> pipePairs;
    double scale;
    int frameWidth;
    int  frameHeight;
    JButton closeButton;

    public GameGraphics(GameBackendPlayer gameBackend, Resolution resolution){
        setWindow(resolution);
        gameState = GameState.getInstance();

        closeButton = new JButton("Stop game");
        closeButton.setBounds(10, 10, 100, 30);




        this.gameBackend = gameBackend;
        bird = gameBackend.bird;
        pipePairs = gameBackend.pipePairs;

        makeShitCanvas = new RenderingPanel();

        makeShitCanvas.setLayout(null);
        makeShitCanvas.add(closeButton);

        add(makeShitCanvas);
        setupKeyBindings();
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


    private void setupKeyBindings() {
        InputMap inputMap = makeShitCanvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = makeShitCanvas.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "jumpAction");
        actionMap.put("jumpAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBackend.userJumped = true;
            }
        });


        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameState.setGameOver(true);
            }
        });
    }

    private class RenderingPanel extends JPanel {
        int birdWidth = 50;
        int birdHeight = 50;
        int pipeWidth = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            g.setColor(Color.RED);
            g.fillRect((int) ((bird.coordinateX - (birdWidth / 2)) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) (birdWidth * scale), (int) (birdHeight * scale));

            g.setColor(Color.BLUE);
            int circleRadius = 25; // Adjust the radius as needed
            g.fillOval((int) ((bird.coordinateX - circleRadius) * scale), (int) ((bird.coordinateY - (birdHeight / 2)) * scale), (int) ((circleRadius * 2) * scale), (int) ((circleRadius * 2) * scale));

            g.setColor(Color.GREEN);
            for (PipePair element : pipePairs) {
                int upperPipeHeight = (int) (element.getUpperY() * scale);
                int lowerPipeHeight = (int) (element.getLowerY() * scale);
                g.fillRect((int) ((element.position - pipeWidth/2) * scale), 0, (int) (pipeWidth * scale),  upperPipeHeight);
                g.fillRect((int) ((element.position - pipeWidth/2) * scale), frameHeight - lowerPipeHeight , (int) (pipeWidth * scale), lowerPipeHeight);
            }
        }
    }
}
