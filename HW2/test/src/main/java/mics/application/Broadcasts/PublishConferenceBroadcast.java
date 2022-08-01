package mics.application.Broadcasts;

import mics.Broadcast;
import mics.application.objects.Student;

import java.util.Map;

public class PublishConferenceBroadcast implements Broadcast {
    private Map<Student, Integer> papersPublishedByStudent;

    public PublishConferenceBroadcast(Map<Student, Integer> map){
        papersPublishedByStudent=map;

    }
    public String getSenderName() {
        return null;
    }
    public Map<Student,Integer> getPapersPublishedByStudent(){
        return papersPublishedByStudent;
    }
}
