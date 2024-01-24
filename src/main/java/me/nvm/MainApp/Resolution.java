package me.nvm.MainApp;

public enum Resolution {
    nHD(0.5),
    SD(1),
    FHD(1.5),
    WQHD(2),
    UHD_4K(3);

    public final double scaleFromSD;

    Resolution(double scaleFromSD) {
        this.scaleFromSD = scaleFromSD;
    }


    @Override
    public String toString() {
        return getWidth() + "/" + getHeight();
    }

    public int getWidth(){
        return (int) (1280 * scaleFromSD);
    }
    public int getHeight(){
        return (int) (720 * scaleFromSD);
    }
}
