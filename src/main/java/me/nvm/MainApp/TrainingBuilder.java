package me.nvm.MainApp;


import me.nvm.game.Game;

public class TrainingBuilder {

    private Integer sizeOfGeneration;
    private int[] structure;
    private Integer numOfElites;
    private Integer numOfRuns;
    private Double mutationStrength;
    private Double mutationRate;
    private Integer numOfElitesPrinted;

    private Game game;

    public TrainingBuilder setSizeOfGeneration(int sizeOfGeneration) {
        this.sizeOfGeneration = sizeOfGeneration;
        return this;
    }

    public TrainingBuilder setStructure(int[] structure) {
        this.structure = structure;
        return this;
    }

    public TrainingBuilder setNumOfElitesPrinted(int numOfElitesPrinted) {
        this.numOfElitesPrinted = numOfElitesPrinted;
        return this;
    }

    public TrainingBuilder setNumOfElites(int numOfElites) {
        this.numOfElites = numOfElites;
        return this;
    }

    public TrainingBuilder setNumOfRuns(int numOfRuns) {
        this.numOfRuns = numOfRuns;
        return this;
    }

    public TrainingBuilder setMutationStrength(double mutationStrength) {
        this.mutationStrength = mutationStrength;
        return this;
    }

    public TrainingBuilder setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        return this;
    }

    public TrainingBuilder linkAddGameObject(Game game){
        this.game = game;
        return this;
    }

    public Training build() {
        if (AuxilaryTools.isNegative(sizeOfGeneration)) {
            throw new IllegalStateException("Size of generation must be a positive number");
        }
        if (structure == null) {
            throw new IllegalStateException("Structure is not set");
        }else if (structure[0] != 4 || structure[structure.length - 1] != 1){
            throw new IllegalStateException("Structure[0] must == 4 and structure[structure.length] == 2");
        }
        if (AuxilaryTools.isNegative(numOfElites)) {
            throw new IllegalStateException("Number of elites must be a non-negative number");
        }
        if (AuxilaryTools.isNegative(numOfRuns)) {
            throw new IllegalStateException("Number of runs must be a positive number");
        }
        if (AuxilaryTools.isNegative(mutationStrength)) {
            throw new IllegalStateException("Mutation strength must be a positive number");
        }
        if (AuxilaryTools.isNegative(mutationStrength) || mutationRate > 1) {
            throw new IllegalStateException("Mutation rate must be between 0 and 1");
        }
        if (game == null) {
            throw new IllegalStateException("Game object is not set");
        }
        if (AuxilaryTools.isNegative(numOfElitesPrinted)) {
            throw new IllegalStateException("Number of elites printed must be a non-negative number");
        }


        return new Training(sizeOfGeneration, structure, numOfElites, numOfRuns, mutationStrength, mutationRate, game, numOfElitesPrinted);
    }
}
