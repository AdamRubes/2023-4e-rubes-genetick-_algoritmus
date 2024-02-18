package me.nvm.MainApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import me.nvm.GNN.Client;
import me.nvm.GNN.GeneticInfo;
import me.nvm.game.Game;
import me.nvm.game.GameState;
import org.controlsfx.control.ListSelectionView;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import static me.nvm.GNN.GenomProcessor.loadGenom;
import static me.nvm.MainApp.AuxilaryTools.getFilenamesInDirectory;

public class MainWindowController {
    Game game;

    Training training;

    File folderWithGenomes;

    @FXML
    private AnchorPane toBeStyled;

    @FXML
    private AnchorPane GameControlsPane;

    @FXML
    private AnchorPane MainPane;

    @FXML
    private TextField elitesToBeSavedTextField;

    @FXML
    private CheckBox saveGenomsBox;


    @FXML
    private TextField numElitesTextField;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label birdsLabel;

    @FXML
    private TextField numRunsTextField;

    @FXML
    private TextField sizeGenerationTextField;

    @FXML
    private TextField structureTextField;

    @FXML
    private TextField rateMutationText;

    @FXML
    private TextField strengthMutationText;

    @FXML
    private ComboBox<Resolution> gameResolutionBox;

    @FXML
    private ComboBox<Double> gameSpeedBox;

    @FXML
    private ListSelectionView<Label> clientList;

    @FXML
    private CheckBox headlessCheck;

    @FXML
    private Label pathLabel;


    @FXML
    void startGameOnAction(ActionEvent event) {
        game = prepareGame();
        game.startPlayerGameReworked();
    }

    @FXML
    void endGameOnAction(ActionEvent event) {
        game.stopGame();
    }

    @FXML
    void pauseGameOnAction(ActionEvent event) {
        //ToDo Implement
        System.out.println("TO be implemented");
    }

    @FXML
    void StartLoadedGameOnAction(ActionEvent event) {
        game = prepareGame();

        if(clientList.getTargetItems().isEmpty()){
            return;
        }

        HashMap<Integer,Client> clientHashMap = new HashMap<>();

        for (Label label: clientList.getTargetItems()){
            String name = label.getText();

            GeneticInfo geneticInfo = loadGenom(FolderHolder.customFolder.getAbsolutePath()+"/" + name);
            Client client = new Client(geneticInfo);

            clientHashMap.put(client.id,client);
        }

        game.startTrainingGame(clientHashMap);
    }

    @FXML
    void refreshListOnAction(ActionEvent event) {
        clientList.getSourceItems().clear();
        clientList.getTargetItems().clear();
        refreshList();
    }

    @FXML
    void startTrainingOnAction(ActionEvent event) {
        int sizeOfGeneration = Integer.parseInt(sizeGenerationTextField.getText());
        int numberOfElites = Integer.parseInt(numElitesTextField.getText());
        int numberOfRuns = Integer.parseInt(numRunsTextField.getText());
        double mutationStrength = Double.parseDouble(strengthMutationText.getText());
        double mutationRate = Double.parseDouble(rateMutationText.getText());
        int numOfElitesPrinted = prepSaves();

        String structureStr = structureTextField.getText();

        String[] stringArray = structureStr.split(",");

        int[] structure = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            structure[i] = Integer.parseInt(stringArray[i].trim());
        }

        Resolution resolution = gameResolutionBox.getValue();

/*
        System.out.println(sizeOfGeneration);
        System.out.println(numberOfElites);
        System.out.println(numberOfRuns);
        System.out.println(mutationStrength);
        System.out.println(mutationRate);
        System.out.println(Arrays.toString(structure));
        System.out.println(resolution.getWidth());
        System.out.println(resolution.getHeight());
 */

        System.out.println(numOfElitesPrinted);
        game = prepareGame();

        //ToDo ukládání genomů regulace

        training = new TrainingBuilder()
                .setSizeOfGeneration(sizeOfGeneration)
                .setNumOfElites(numberOfElites)
                .setNumOfRuns(numberOfRuns)
                .setStructure(structure)
                .setMutationRate(mutationRate)
                .setMutationStrength(mutationStrength)
                .setNumOfElitesPrinted(numOfElitesPrinted)
                .linkAddGameObject(game)
                .build();

        training.start();
    }

    public Game prepareGame(){

        return new GameBuilder()
                .setPredefinedRules()
                .setHeadless(headlessCheck.isSelected())
                .setSpeedMultiplier(gameSpeedBox.getValue())
                .setResolution(gameResolutionBox.getValue())
                .build();
    }

    @FXML
    void saveGenomsOnAction(ActionEvent event) {
        if (training != null){
            training.numOfElitesPrinted = prepSaves();
        }
    }

    @FXML
    void elitesToBeSavedOnAction(ActionEvent event) {
        training.numOfElitesPrinted = prepSaves();
    }

    @FXML
    void stopTrainingOnAction(ActionEvent event) {
        training.stopAfterCurrLoop();
    }

    @FXML
    void stopNowTrainingOnAction(ActionEvent event) {
        training.stopTrainingNow();
    }

    @FXML
    void endRunOnAction(ActionEvent event) {
        training.stopCurrRun();
    }

    @FXML
    void gameSpeedChanged(ActionEvent event) {if (game != null) game.changeSpeedOfGame(gameSpeedBox.getValue());
    }

    @FXML
    void resolutionChanged(ActionEvent event) {if (game != null)  game.changeResolution(gameResolutionBox.getValue());}


    @FXML
    public void changeDirectoryOnAction(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(FolderHolder.USER_HOME);

        FolderHolder.customFolder = directoryChooser.showDialog(new Stage());
        pathLabel.setText(FolderHolder.customFolder.getPath());

        refreshList();
    }


    public int prepSaves(){
        int numOfElitesPrinted;
        if(saveGenomsBox.isSelected()){
            numOfElitesPrinted = Integer.parseInt(elitesToBeSavedTextField.getText());
        }else numOfElitesPrinted = 0;

        return numOfElitesPrinted;
    }

    public void refreshList(){
        List<String> clientSource = getFilenamesInDirectory(FolderHolder.customFolder.getPath());
        clientSource.remove("forGitUwU");

        ObservableList<Label> observableClientSourceLabels = FXCollections.observableArrayList();

        for (String fileName : clientSource) {
            Label label = new Label(fileName);
            observableClientSourceLabels.add(label);
        }
        clientList.getSourceItems().addAll(observableClientSourceLabels);
    }

    public void initialize() {
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setParent(toBeStyled);

        FolderHolder.autoInitFile();

        pathLabel.setText(FolderHolder.customFolder.getPath());


        GameState gameState = GameState.getInstance();

        scoreLabel.textProperty().bind(gameState.scoreProperty.asString());

        birdsLabel.textProperty().bind(gameState.numOfLivingBirdsProperty.asString());

        refreshList();

        gameResolutionBox.getItems().addAll(
                Resolution.nHD, Resolution.SD, Resolution.FHD, Resolution.WQHD, Resolution.UHD_4K
        );
        gameResolutionBox.setValue(Resolution.SD);

        gameSpeedBox.getItems().addAll(
                0.1, 0.5, 1.0, 1.5, 2.0, 4.0, 8.0, 12.0, 24.0, 48.0
        );

        gameSpeedBox.setValue(1.0);
    }
}