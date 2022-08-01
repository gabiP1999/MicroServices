package mics.application;

import mics.Future;
import mics.Message;
import mics.MessageBusImpl;
import mics.MessageBusSingleton;
import mics.application.Events.TestModelEvent;
import mics.application.Events.TrainModelEvent;
import mics.application.objects.*;
import com.google.gson.*;
import mics.application.services.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */


public class CRMSRunner {

    public static void main(String[] args)
    {
        //MessageBusTests();

        MessageBusImpl bus = MessageBusSingleton.getInstance();
        Cluster cluster = Cluster.getInstance();
        String path_to_input ="\\test\\src\\main\\resources\\example_input.json";
        String current_dir = System.getProperty("user.dir");
        String path = current_dir+path_to_input;
        System.out.println(path);
        File input =new File(path);
        readFromJson(input);


    }


    private static void readFromJson(File input) {
        try
        {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();

            List<Student> students = createStudents(fileObject);
            List<GPU> GPUS = createGPUS(fileObject);
            List<CPU> CPUS = createCPUS(fileObject);
            List<ConfrenceInformation> conferences = createConferences(fileObject);

            TimeService timeService = makeTimeService(fileObject);
            Thread t = new Thread(timeService); t.start();
            List<ConferenceService> ConferenceServices = makeConferenceServices(conferences);
            for(ConferenceService c: ConferenceServices){
                Thread thread = new Thread(c);
                thread.start();
            }
            List<CPUService> CPUSServices = makeCPUSServices(CPUS);
            for(CPUService c: CPUSServices){
                Thread thread = new Thread(c);
                thread.start();
            }
            List<GPUService> GPUSServices = makeGPUSServices(GPUS);
            for(GPUService c: GPUSServices){
                Thread thread = new Thread(c);
                thread.start();
            }
            List<StudentService> newStudentServices = makeStudentsServices(students);
            for(StudentService studentService : newStudentServices){
                Thread thread = new Thread(studentService);
                thread.start();
            }

            while(t.isAlive()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Running is over");
            CreateOutputFile(ConferenceServices,CPUSServices,GPUSServices,newStudentServices);


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private static void CreateOutputFile(List<ConferenceService> conferenceServices, List<CPUService> cpusServices, List<GPUService> gpusServices, List<StudentService> newStudentServices) {
        String path_to_output ="\\test\\src\\main\\resources\\Output.txt";
        String current_dir = System.getProperty("user.dir");
        String output_path = current_dir+path_to_output;
        //String output_path = "C:\\Users\\gabi4\\OneDrive\\Desktop\\SPL\\HW2\\test\\src\\main\\resources\\Output.txt";
        CreateFile(output_path);
        System.out.println("Writing to the File");
        try {
            FileWriter myWriter = new FileWriter(output_path);
            myWriter.write("  # # # # # # # # # # # #\n");
            //Students
            myWriter.write(" Students:  #\n");
            for(StudentService s : newStudentServices){
                myWriter.write(" # # # # # # # # # # # #\n");
                myWriter.write("    Name: "+s.getStudentName()+"       #\n");
                myWriter.write("    Department: "+s.getStudent().getDepartment()+"       #\n");
                myWriter.write("    Status: "+s.getStudent().getStatus()+"       #\n");
                myWriter.write("    Publications: "+s.getStudent().getPublications()+"       #\n");
                myWriter.write("    Papers Read: "+s.getStudent().getPapersRead()+"       #\n");
                myWriter.write("    Trained Models:        #\n");
                for(Model m :s.getOutputModels()){
                    myWriter.write("            Name:"+m.getName()+"          #\n");
                    myWriter.write("            Data:"+"            #\n");
                    myWriter.write("                Type:"+m.getData().getType()+"            #\n");
                    myWriter.write("                Size:"+m.getData().getSize()+"            #\n");
                    myWriter.write("            Status:"+m.getStatus()+"            #\n");
                    myWriter.write("            Results:"+m.getResults()+"            #\n");
                }
            }
            myWriter.write("  # # # # # # # # # # # #\n");
            myWriter.write(" Conferences:     #\n");
            for(ConferenceService s : conferenceServices){
                myWriter.write("  # # # # # # # # # # # #\n");
                myWriter.write("    Name:"+s.getConferenceName()+"       #\n");
                myWriter.write("    Date:"+s.getConferenceDate()+"       #\n");
                myWriter.write("    Publications:       #\n");
                for(List<Model> list : s.getModelsByStudent().values()){
                    for(Model m:list){
                        myWriter.write("            Name:"+m.getName()+"          #\n");
                        myWriter.write("            Data:"+"           #\n");
                        myWriter.write("                Type:"+m.getData().getType()+"              #\n");
                        myWriter.write("                Size:"+m.getData().getSize()+"              #\n");
                        myWriter.write("            Status:"+m.getStatus()+"            #\n");
                        myWriter.write("            Results:"+m.getResults()+"            #\n");
                    }
                }
            }
            int cpu_time =0;
            int gpu_time =0;
            int data_batches = 0;
            for(CPUService s : cpusServices){
                cpu_time+=s.getTimeUsed();
                data_batches+=s.getBatchesProcessed();
            }
            for(GPUService s : gpusServices){
                gpu_time+=s.getTimeUse();
            }
            myWriter.write("  # # # # # # # # # # # #\n");
            myWriter.write(" CPU time use: "+cpu_time+"    #\n");
            myWriter.write(" Data Batches Processed: "+data_batches+"    #\n");
            myWriter.write(" GPU time use: "+gpu_time+"    #\n");
            myWriter.write("  # # # # # # # # # # # #\n");

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }



    private static void CreateFile(String output_path) {
        System.out.println("Creating output file");
        try {
            File myObj = new File(output_path);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static TimeService makeTimeService(JsonObject fileObject) {
        JsonElement jsonTickTime = fileObject.get("TickTime");
        int TickTime = jsonTickTime.getAsInt();
        JsonElement jsonDuration = fileObject.get("Duration");
        int Duration = jsonDuration.getAsInt();

        System.out.println(TickTime+" "+Duration);

        TimeService timeService =  new TimeService("timeService",TickTime,Duration );
        return timeService;
    }

    private static List<ConferenceService> makeConferenceServices(List<ConfrenceInformation> conferences) {
        List<ConferenceService> ConferenceServices = new ArrayList<ConferenceService>();
        int i=0;
        for (ConfrenceInformation conf :conferences)
        {
            ConferenceServices.add(new ConferenceService("ConferenceService "+i,conf.getDate(),conf.getName()));
            i++;
        }
        return ConferenceServices;
    }

    private static List<CPUService> makeCPUSServices(List<CPU> cpus) {
        List<CPUService> CPUSServices = new ArrayList<CPUService>();
        int i=0;
        for (CPU cpu:cpus)
        {
            CPUSServices.add(new CPUService("CPUSService "+i,cpu.getCores()));
            i++;
        }
        return CPUSServices;
    }

    private static List<GPUService> makeGPUSServices(List<GPU> gpus) {
        List<GPUService> GPUSServices = new ArrayList<GPUService>();
        int i=0;
        for (GPU gpu:gpus)
        {
            GPUSServices.add(new GPUService("GPUSService "+i,gpu.getType()));
            i++;
        }
        return GPUSServices;
    }

    private static List<StudentService> makeStudentsServices(List<Student> students) {
        List<StudentService> newStudentServices = new ArrayList<StudentService>();
        int i=0;
        for (Student s:students)
        {
            newStudentServices.add(new StudentService("studentService "+i,s));
            i++;
        }
        return newStudentServices;
    }


    private static List<Student> createStudents(JsonObject fileObject) {
        List<Student> newStudents = new ArrayList<Student>();
        JsonArray jsonStudents = fileObject.get("Students").getAsJsonArray();
        for(JsonElement JsonStudent : jsonStudents ) {
            JsonObject student = JsonStudent.getAsJsonObject();
            String name = student.get("name").getAsString();
            String department = student.get("department").getAsString();
            String s = student.get("status").getAsString();
            Student.Degree status= Student.Degree.valueOf(s);
            List<Model> models = createModels(student);
            Student newStudent = new Student(models,name,department,status);
            newStudents.add(newStudent);

            System.out.println(name+" "+department+" "+status);
        }
        return newStudents;
    }

    private static List<Model> createModels(JsonObject student) {
        List<Model> models = new ArrayList<Model>();
        JsonArray jsonModels = student.get("models").getAsJsonArray();
        for(JsonElement JsonModel : jsonModels ) {
            JsonObject model = JsonModel.getAsJsonObject();
            String name = model.get("name").getAsString();
            String s1 = model.get("type").getAsString();
            Data.Type type= Data.Type.valueOf(s1);
            int size = model.get("size").getAsInt();
            models.add(new Model(new Data(size,type),name));

            System.out.println(size+" "+type+" "+name);
        }
        return models;
    }

    private static List<CPU> createCPUS(JsonObject fileObject) {
        Gson gson = new Gson();
        List<CPU> CPUS = new ArrayList<CPU>();
        JsonArray jsonCPUS = fileObject.get("CPUS").getAsJsonArray();
        for(JsonElement JsonCPU : jsonCPUS ) {
            int cores = JsonCPU.getAsInt();
            CPUS.add(new CPU(cores));

            System.out.println(cores);
        }
        return CPUS;
    }

    private static List<GPU> createGPUS(JsonObject fileObject) {
        List<GPU> GPUS = new ArrayList<GPU>();
        JsonArray jsonGPUS = fileObject.get("GPUS").getAsJsonArray();
        for(JsonElement JsonGPU : jsonGPUS ) {
            String s2 = JsonGPU.getAsString();
            GPU.Type type = GPU.Type.valueOf(s2);
            GPUS.add(new GPU(type));

            System.out.println(type);
        }
        return GPUS;
    }

    private static List<ConfrenceInformation> createConferences(JsonObject fileObject) {
        List<ConfrenceInformation> conferences = new ArrayList<ConfrenceInformation>();
        JsonArray jsonConferences = fileObject.get("Conferences").getAsJsonArray();
        for(JsonElement JsonConference : jsonConferences ) {
            JsonObject conference = JsonConference.getAsJsonObject();
            String name = conference.get("name").getAsString();
            int date = conference.get("date").getAsInt();
            conferences.add(new ConfrenceInformation(name,date));

            System.out.println(name+" "+date);
        }
        return conferences;
    }

    public static void MessageBusTests()
    {
        Data data = new Data(20000, Data.Type.Images);
        Data data2 = new Data(1000, Data.Type.Text);
        Model model = new Model(data,"model 1");
        Model model2 = new Model(data2,"model 2");
        Student s = new Student(new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD);
        TrainModelEvent e = new TrainModelEvent(model,s);
        GPUService gpuService = new GPUService("GPU Service", GPU.Type.RTX2080);
        StudentService studentService = new StudentService("Student Service",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        MessageBusImpl bus = MessageBusSingleton.getInstance();
        bus.register(gpuService);
        bus.register(studentService);

        gpuService.subscribeEvent(TrainModelEvent.class,ev->{
            System.out.println("gpu service is handling trainmodelevent");
        });
        System.out.println(bus.isRegistered(gpuService));//true
        System.out.println(bus.isRegistered(studentService));//true
        System.out.println(bus.isSubscribedToEvent(TrainModelEvent.class,gpuService));//true
        System.out.println(bus.isSubscribedToEvent(TrainModelEvent.class,studentService));//false
        System.out.println(bus.isSubscribedToEvent(TestModelEvent.class,gpuService));//false
        Future<Model> future = studentService.sendEvent(e);
        System.out.println(bus.wasEventSent(e));//true
        try {
            Message message = bus.awaitMessage(gpuService);
            TrainModelEvent event = (TrainModelEvent)message;
            System.out.println(event.getSenderName());//gabi
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        System.out.println(future.isDone());//false
        bus.complete(e,model2);
        System.out.println(future.isDone());//true
        System.out.println(future.get().getName());//model 2
        GPUService gpuService2 = new GPUService("GPU Service 2",GPU.Type.RTX2080);
        bus.register(gpuService2);
        Model model3 = new Model(data2,"Model 3");
        Student student2 = new Student(new ArrayList<Model>(),"Student 2","bgu", Student.Degree.PhD);
        bus.sendEvent(new TrainModelEvent(model3,student2));
    }
}