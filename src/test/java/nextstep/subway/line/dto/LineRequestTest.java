package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.wrappers.Sections;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.wrappers.LineStations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.wrapper.Distance;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 파라미터 테스트")
class LineRequestTest {

    private LineRequest lineRequest;

    @BeforeEach
    void setUp() {
        lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 100);
    }

    @Test
    @DisplayName("지하철 노선 파라미터 객체 생성")
    void create() {
        assertThat(lineRequest).isEqualTo(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 100));
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 Line entity 정상 생성")
    void toLine() {
        assertThat(lineRequest.toLine()).isEqualTo(new Line("신분당선", "bg-red-600"));
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 section entity 생성")
    void toSection() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        Section expected = new Section(upStation, downStation, lineRequest.getDistance());

        assertThat(lineRequest.toSection(upStation, downStation)).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 LineStations 생성")
    void toLineStations() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        LineStations expected = new LineStations(
                Arrays.asList(new LineStation(upStation, null, 0), new LineStation(downStation, upStation, 100)));
        assertThat(lineRequest.toLineStations(upStation, downStation)).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 Line entity 정상 생성 - 구간, 노선 지하철역 연결 테이블 eitity 추가")
    void toLine2() {
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        LineStations lineStations = lineRequest.toLineStations(upStation, downStation);

        Section section = lineRequest.toSection(upStation, downStation);
        Line line = lineRequest.toLine(section, lineStations);
        assertThat(line).isEqualTo(new Line("신분당선", "bg-red-600", new Sections(Arrays.asList(section)), lineStations));
    }
}