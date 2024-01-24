package me.nvm.MainApp;

public class UniqueIDGenerator {
    static int lastID = 0;

    public static int getRandomID(){
        lastID++;
        return lastID;
    }

}
