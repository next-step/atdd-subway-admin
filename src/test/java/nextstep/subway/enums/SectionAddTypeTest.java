package nextstep.subway.enums;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.domain.wrappers.LineStations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SectionAddTypeTest {

    private LineStations lineStations;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
        정자역 = new Station(3L, "정자역");
        lineStations = new LineStations(Arrays.asList(
                new LineStation(강남역, null, 0),
                new LineStation(양재역, 강남역, 10),
                new LineStation(정자역, 양재역, 10)
        ));
    }

    @Test
    void section_저장_타입_NEW_UP() {
        LineStation lineStation = new LineStation(강남역, new Station(4L, "교대역"), 10);
        assertThat(SectionAddType.calcAddType(lineStations, lineStation)).isEqualTo(SectionAddType.NEW_UP);
    }

    @Test
    void section_저장_타입_NEW_DOWN() {
        LineStation lineStation = new LineStation(new Station(7L, "미금역"), 정자역, 10);
        assertThat(SectionAddType.calcAddType(lineStations, lineStation)).isEqualTo(SectionAddType.NEW_DOWN);
    }

    @Test
    void section_저장_타입_NEW_BETWEEN() {
        LineStation lineStation = new LineStation(new Station(5L, "시민의 숲역"), 강남역, 10);
        assertThat(SectionAddType.calcAddType(lineStations, lineStation)).isEqualTo(SectionAddType.NEW_BETWEEN);
    }
}
