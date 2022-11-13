package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SectionRepository 테스트")
@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("line으로 section 목록을 검색한다.")
    void findByLine() {
        Station 교대역 = stationRepository.save(new Station("교대역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));

        Line line = new Line("2호선", "bg-green-600");
        line.addSection(new Section(교대역, 강남역, 10));
        line.addSection(new Section(강남역, 역삼역, 10));
        lineRepository.save(line);

        List<Section> sections = sectionRepository.findByLine(line);

        assertThat(sections).hasSize(2);
    }
}
