package me.nvm.game.gameobjects;

public class PipePair {
    public Pipe upPipe;
    public Pipe downPipe;
    public double position;

    public PipePair(int upPipe, int downPipe, int position) {
        this.position = position;
        this.upPipe = new Pipe(true, position, upPipe);
        this.downPipe = new Pipe(false, position, downPipe);
    }


    public int getUpperY(){
        return (int) upPipe.coordinateY;
    }

    public int getLowerY(){
        return (int) downPipe.coordinateY;
    }


    public void move(){
        upPipe.movePipe();
        downPipe.movePipe();
        position = (int) downPipe.coordinateX;
    }

    @Override
    public String toString() {
        return "PipePair{" +
                "position=" + position +
                '}';
    }
}
