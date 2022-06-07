package nextstep.subway.line.domain;

import nextstep.subway.station.domain.StationTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineTest {
    public static final Line 이호선 = new Line(1L, "2호선", "bg-green-600", StationTest.사당역, StationTest.강남역, 10L);
    public static final Line 사호선 = new Line(2L, "4호선", "bg-blue-600", StationTest.사당역, StationTest.이수역, 10L);

}