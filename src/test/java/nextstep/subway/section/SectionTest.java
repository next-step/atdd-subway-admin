package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
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
        final Line line = lineRepository.save(LineRequest.of("1호선", "blue").toLine());
        int distance = 5;

        Section section = new Section(line, upStation, downStation, distance);
        final Section savedSection = sectionRepository.save(section);

        Section findSection = sectionRepository.findById(savedSection.getId()).get();
        assertThat(findSection.getStation().getId()).isEqualTo(upStation.getId());
        assertThat(findSection.getNextStation().getId()).isEqualTo(downStation.getId());
        assertThat(findSection.getLine().getId()).isEqualTo(line.getId());
    }

}
