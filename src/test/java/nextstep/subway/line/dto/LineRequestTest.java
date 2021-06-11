package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 파라미터 테스트")
class LineRequestTest {

    @Test
    @DisplayName("지하철 노선 파라미터 객체 생성")
    void create() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 100);
        assertThat(lineRequest).isEqualTo(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 100));
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 Line entity 정상 생성")
    void toLine() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600");
        assertThat(lineRequest.toLine()).isEqualTo(new Line("분당선", "bg-red-600"));
    }

    @Test
    @DisplayName("지하철 노선 파라미터를 이용하여 section entity 생성")
    void toSection() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 100);
        Station upStation = new Station("강남역");
        Station downStation = new Station("광교역");
        Section expected = new Section(upStation, downStation, lineRequest.getDistance());

        assertThat(lineRequest.toSection(upStation, downStation)).isEqualTo(expected);
    }
}