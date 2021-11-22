package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName : nextstep.subway.section
 * fileName : SectionTEst
 * author : haedoang
 * date : 2021/11/20
 * description :
 */
@SpringBootTest
public class SectionTest {
    private static final int DISTANCE_5 = 5;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SectionRepository sectionRepository;

    @DisplayName("구간을 생성한다.")
    @Test
    void create() {
        final Station upStation = stationRepository.save(StationRequest.of("강남역").toStation());
        final Station downStation = stationRepository.save(StationRequest.of("삼성역").toStation());
        Line line = new Line("2호선", "녹색");
        line.addSection(new Section(line, upStation, downStation, Distance.of(DISTANCE_5)));
        final Line savedLine = lineRepository.save(line);

        Section findSection = sectionRepository.findById(savedLine.getSections().get(0).getId()).get();
        assertThat(findSection.getStation().getId()).isEqualTo(upStation.getId());
        assertThat(findSection.getNextStation().getId()).isEqualTo(downStation.getId());
        assertThat(findSection.getLine().getId()).isEqualTo(line.getId());
    }

    @DisplayName("노선의 역 목록 조회")
    @Test
    void getStations() {
        final Distance 거리_5 = Distance.of(5);
        final Line 일호선 = new Line("일호선","blue");
        final Station 서울역 = new Station("서울역");
        final Station 시청역 = new Station("시청역");
        Section 서울역_시청_구간 = new Section(일호선, 서울역, 시청역, 거리_5);
        일호선.addSection(서울역_시청_구간);

        final Station 종각역 = new Station("종각역");
        Section 시청_종각역_구간 = new Section(일호선, 시청역, 종각역, 거리_5);
        일호선.addSection(시청_종각역_구간);

        List<Station> stations = 일호선.getStations();

        assertThat(stations).hasSize(3);
        assertThat(stations).contains(서울역, 시청역, 종각역);
    }
}
