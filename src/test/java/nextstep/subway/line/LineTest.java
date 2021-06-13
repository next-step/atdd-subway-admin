package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능 객체 테스트")
public class LineTest {
    private Line 신분당선;
    private Station 양재역;
    private Station 수지구청역;

    @DisplayName("노선에 구간 추가하기")
    @Test
    void addSections() {
        노선_생성();

        Station upStation = 상행_구간_등록("강남역", 양재역, 30);
        Station downStation = 하행_구간_등록(양재역, "판교역", 20);

        List<Station> result = 신분당선.getStations();

        assertThat(result).contains(upStation);
        assertThat(result).contains(downStation);
    }

    private void 노선_생성() {
        양재역 = new Station("양재역");
        수지구청역 = new Station("수지구청역");
        신분당선 = new Line("신분당선", "red", 양재역, 수지구청역, 300);
    }

    private Station 상행_구간_등록(String upStationName, Station downStationName, int distance) {
        Station upStation = new Station(upStationName);
        Section section = new Section(신분당선, upStation, downStationName, distance);
        신분당선.addSection(section);
        return upStation;
    }

    private Station 하행_구간_등록(Station upStation, String downStationName, int distance) {
        Station downStation = new Station(downStationName);
        Section section = new Section(신분당선, upStation, downStation, distance);
        신분당선.addSection(section);
        return downStation;
    }
}
