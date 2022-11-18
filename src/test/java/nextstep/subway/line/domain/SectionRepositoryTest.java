package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SectionRepository 테스트")
@DataJpaTest
class SectionRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Line line;

    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        line = new Line("2호선", "bg-green-600");
    }

    @Test
    @DisplayName("line으로 section 목록을 검색한다.")
    void findByLine() {
        line.addSection(new Section(교대역, 강남역, 10));
        line.addSection(new Section(강남역, 역삼역, 10));
        lineRepository.save(line);

        List<Section> sections = sectionRepository.findAllByLine(line);

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("현재 노선의 구간에서 요청된 상행역, 하행역 중 하나라도 일치하는 구간 목록을 검색한다.")
    void findAllByRequestedSection() {
        line.addSection(new Section(교대역, 강남역, 10));
        line.addSection(new Section(강남역, 역삼역, 10));
        lineRepository.save(line);

        List<Section> sections = sectionRepository.findAllByRequestedSection(교대역, 역삼역);

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("요청된 역을 상행역으로 포함하고 있는 노선을 검색한다.")
    void findByUpStationId() {
        Section expected = new Section(강남역, 역삼역, 10);
        line.addSection(new Section(교대역, 강남역, 10));
        line.addSection(expected);
        lineRepository.save(line);

        Optional<Section> actual = sectionRepository.findByUpStationId(강남역.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertTrue(expected.equals(actual.get()))
        );
    }

    @Test
    @DisplayName("요청된 역을 하행역으로 포함하고 있는 노선을 검색한다.")
    void findByDownStationId() {
        Section expected = new Section(교대역, 강남역, 10);
        line.addSection(expected);
        line.addSection(new Section(강남역, 역삼역, 10));
        lineRepository.save(line);

        Optional<Section> actual = sectionRepository.findByDownStationId(강남역.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertTrue(expected.equals(actual.get()))
        );
    }
}
