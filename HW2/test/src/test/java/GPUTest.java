import mics.application.objects.Data;
import mics.application.objects.DataBatch;
import mics.application.objects.GPU;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GPUTest {

    private GPU gpu;
    Data data;
    DataBatch dataBatch;
    @Test
    @BeforeEach
    void setUp() {
        gpu = new GPU(GPU.Type.RTX2080);
        data = new Data(10, Data.Type.Images);
        dataBatch = new DataBatch(data,0);
    }
    void getCurrent_dataBatch() {
        gpu.setCurrent_dataBatch(dataBatch,4);
        gpu.getCurrent_dataBatch();
        Assertions.assertEquals(null,gpu.getCurrent_dataBatch());
    }

    @Test
    void setCurrent_dataBatch() {
        gpu.setCurrent_dataBatch(dataBatch,4);
        Assertions.assertEquals(gpu.getCurrent_dataBatch(),dataBatch);
    }

    @Test
    void isReady() {
        gpu.setCurrent_dataBatch(dataBatch,4);
        gpu.tick();
        gpu.tick();
        gpu.tick();
        gpu.tick();
        Assertions.assertEquals(true,gpu.isReady());
    }

    @Test
    void tick() {
        gpu.tick();
        Assertions.assertEquals(1,gpu.getClock());
    }
}