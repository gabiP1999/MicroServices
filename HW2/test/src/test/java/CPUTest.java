import mics.application.objects.CPU;
import mics.application.objects.Data;
import mics.application.objects.DataBatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CPUTest {

    private CPU cpu;
    Data data;
    DataBatch dataBatch;
    @BeforeEach
    void setUp() {
        cpu = new CPU(32);
        data = new Data(10, Data.Type.Images);
        dataBatch = new DataBatch(data,0);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCurrent_dataBatch() {
        cpu.setCurrent_dataBatch(dataBatch);
        cpu.getCurrent_dataBatch();
        Assertions.assertEquals(null,cpu.getCurrent_dataBatch());
    }

    @Test
    void setCurrent_dataBatch() {
        cpu.setCurrent_dataBatch(dataBatch);
        Assertions.assertEquals(cpu.getCurrent_dataBatch(),dataBatch);
    }

    @Test
    void isReady() {
        cpu.setCurrent_dataBatch(dataBatch);
        cpu.tick();
        cpu.tick();
        cpu.tick();
        cpu.tick();
        cpu.tick();
        Assertions.assertEquals(true,cpu.isReady());
    }

    @Test
    void tick() {
        cpu.tick();
        Assertions.assertEquals(1,cpu.getClock());
    }
}