package nextstep.subway.enums;

import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionAddTypeTest {

    private List<LineStation> lineStations;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        양재역 = new Station(2L, "양재역");
        정자역 = new Station(3L, "정자역");
        lineStations = Arrays.asList(
                new LineStation(강남역, null, 0),
                new LineStation(양재역, 강남역, 10),
                new LineStation(정자역, 양재역, 10)
        );
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

    @Test
    void 노선_지하철역_연결_entity를_이용하여_구간_생성() {
        Line line = new Line("신분당선", "bg-red-600");
        LineStation lineStation = new LineStation(양재역, 강남역, 10);
        lineStation.lineBy(line);
        assertThat(SectionAddType.createSection(lineStation)).isEqualTo(new Section(line, 강남역, 양재역, 10));
    }
}
