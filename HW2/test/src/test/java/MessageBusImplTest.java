import mics.*;
import mics.application.objects.Model;
import mics.application.objects.Student;
import mics.application.services.StudentService;
import mics.example.messages.ExampleBroadcast;
import mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class MessageBusImplTest {

    private MessageBusImpl Bus;
    private MicroService m;
    private MicroService l;

    @BeforeEach
    void setUp() {
        Bus = new MessageBusImpl();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void subscribeEvent() {
        m=new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        m.subscribeEvent(ExampleEvent.class,e->{});
        Assertions.assertEquals(true, Bus.isSubscribedToEvent(ExampleEvent.class,m));
    }

    @Test
    void subscribeBroadcast() {
        m= new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        m.subscribeBroadcast(ExampleBroadcast.class,b->{});
        Assertions.assertEquals(true,Bus.isSubscribedToBroadcast(ExampleBroadcast.class,m));
    }

    @Test
    void complete() {
        m= new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        Event<String> e = new ExampleEvent("test_event");
        Future<String> future = m.sendEvent(e);
        Bus.complete(e,"Result");
        Assertions.assertEquals(true,future.isDone());

    }

    @Test
    void sendBroadcast() {
        m= new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        Broadcast b = new Broadcast() {
            @Override
            public String getSenderName() {
                return "La la la";
            }
        };
        m.sendBroadcast(b);
        Assertions.assertEquals(true, Bus.wasBroadcastSent(b));
    }

    @Test
    void sendEvent() {
        m= new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        Event<String> e = new ExampleEvent("test_event");
        Assertions.assertEquals(true, Bus.wasEventSent(e));
    }

    @Test
    void register() {
        m = new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        Assertions.assertEquals(true,Bus.isRegistered(m));
    }

    @Test
    void unregister() {
        m = new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        Bus.unregister(m);
        Assertions.assertEquals(false,Bus.isRegistered(m));
    }

    @Test
    void awaitMessage() throws InterruptedException {

        m= new StudentService("test_student",new Student(
                new ArrayList<Model>(),"gabi","bgu", Student.Degree.PhD));
        Bus.register(m);
        m.subscribeBroadcast(ExampleBroadcast.class,b->{});
        m.sendBroadcast(new ExampleBroadcast("test_broadcast"));
        Broadcast b = (Broadcast) Bus.awaitMessage(m);
        Assertions.assertEquals("test_broadcast",b.getSenderName());

    }




}