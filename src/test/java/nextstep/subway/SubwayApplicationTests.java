package nextstep.subway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.util.Arrays.asList;

@SpringBootTest
class SubwayApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        List<Sample> list = asList(new Sample(5, 4));
        int target = 5;
        list.sort((left, right) -> left.b == target ? -1 : 1);
        System.out.println(list);

    }
}

class Sample {
    int a;
    int b;

    public Sample(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
