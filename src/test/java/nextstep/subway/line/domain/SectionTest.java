package nextstep.subway.line.domain;

import static org.junit.jupiter.api.Assertions.*;

import nextstep.subway.station.domain.StationTest;

public class SectionTest {

    public static final Section SECTION_1 = new Section(LineTest.LINE_2, StationTest.STATION_2, StationTest.STATION_4, 6);
    public static final Section SECTION_2 = new Section(LineTest.LINE_2, StationTest.STATION_4, StationTest.STATION_3, 10);
}