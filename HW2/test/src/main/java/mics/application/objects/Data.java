package mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public Data(int size,Type type){
        this.size=size;
        this.processed=0;
        this.type=type;
    }
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Type getType() {
        return type;
    }
    public int getProcessed(){return processed;}
    public void setProcessed(int processed){
        this.processed=processed;
    }
    public boolean isProcessed(){
        return getSize()==getProcessed();
    }


    public int getSize(){return size;}
}
