package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        line.addSection(new Section(line, upStation, downStation, DISTANCE_5));
        final Line savedLine = lineRepository.save(line);

        Section findSection = sectionRepository.findById(savedLine.getSections().get(0).getId()).get();
        assertThat(findSection.getStation().getId()).isEqualTo(upStation.getId());
        assertThat(findSection.getNextStation().getId()).isEqualTo(downStation.getId());
        assertThat(findSection.getLine().getId()).isEqualTo(line.getId());
    }

    @DisplayName("노선역_목록_조회")
    @Test
    void getStationsTest() {
        // given

    }

}
