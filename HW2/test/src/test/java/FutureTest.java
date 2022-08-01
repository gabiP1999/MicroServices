import mics.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.TimeUnit;

class FutureTest {

    static Future<Integer> f;
    static Integer num;
    static long timeout2;
    static long timeout6;

    @BeforeEach
    void setUp() {
        f= new Future<Integer>();
        num = new Integer(7);
        timeout2 = 2;
        timeout6 = 6;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void getReturnNull() {
        Assertions.assertEquals(f.get(), null);
    }

    @org.junit.jupiter.api.Test
    void resolve() {
        Assertions.assertEquals(f.get(), null);
        Assertions.assertEquals(f.isDone(), false);
        f.resolve(num);
        Assertions.assertEquals(f.get(), num);
        Assertions.assertEquals(f.isDone(), true);
    }

    @org.junit.jupiter.api.Test
    void getReturnInt() {
        f.resolve(num);
        Assertions.assertEquals(f.get(), num);
    }


    @org.junit.jupiter.api.Test
    void isDone() {
        Assertions.assertEquals(f.isDone(), false);
        f.resolve(num);
        Assertions.assertEquals(f.isDone(), true);
    }

    @org.junit.jupiter.api.Test
    void testGetNull()
    {
        Thread t = new Thread(()-> {
            try{ Thread.sleep(2000); }
            catch (Exception e){}
            System.out.println("resolve");
            f.resolve(num);
        }
        );
        t.run();
        Assertions.assertEquals( f.get(timeout6, TimeUnit.SECONDS),num);
    }

    @org.junit.jupiter.api.Test
    void testGetTrue()
    {
        Assertions.assertEquals( f.get(timeout2, TimeUnit.SECONDS),null);
        Thread t = new Thread(()-> {
            try{ Thread.sleep(6000); }
            catch (Exception e){}

            f.resolve(num);
        }
        );
        t.run();
    }
}