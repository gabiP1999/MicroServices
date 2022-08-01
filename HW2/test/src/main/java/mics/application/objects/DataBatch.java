package mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;

    public Data getData() {
        return data;
    }

    public Data.Type getType() {
        return data.getType();
    }

    public DataBatch(Data data, int index){
        this.data=data;
        this.start_index=index;
    }
}
