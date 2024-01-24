package me.nvm.game;

import me.nvm.MainApp.Resolution;

public interface GraphicsInterface {
    public void showWindow();
    public void refresh();
    public void closeWindow();
    public void setResolution(Resolution resolution);
}
