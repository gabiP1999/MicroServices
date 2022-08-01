package mics.application.objects;

import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public Student(List<Model> models,String name,String department,Degree degree){
        modelList=models;

        this.name=name;
        this.department=department;
        this.status=degree;
        publications=0;
        papersRead=0;
    }
    private List<Model> modelList;


    public List<Model> getModels() {
        return modelList;
    }

    public void publish() {
        publications++;
    }

    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    public String getName(){
        return name;
    }
    public String getDepartment(){return department;}
    public Degree getStatus(){return status;}
    public int getPublications(){return publications;}
    public int getPapersRead(){return papersRead;}
    public void addPapersRead(int papers){
        papersRead+=papers;
    }


}
