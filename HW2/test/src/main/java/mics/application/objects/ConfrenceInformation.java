package mics.application.objects;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int clock;
    private int date;
    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date=date;
        clock=0;
    }
    public boolean isReady(){
        return clock>=date;
    }
    public void tick(){
        clock++;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }
}
