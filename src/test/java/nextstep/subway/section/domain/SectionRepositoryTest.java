package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

/**
 * SectionRepository 인터페이스 메소드 검증 테스트
 */
@DataJpaTest
public class SectionRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station upStation;
    private Station downStation;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
        this.stations = stationRepository.saveAll(Arrays.asList(upStation, downStation));

    }

    @Test
    @DisplayName("상행역 ID로 찾기")
    void findByUpStationId() {
        // given
        Section section = new Section(this.upStation, this.downStation, 10);
        Section savedSection = sectionRepository.save(section);

        // when
        Section findSection = sectionRepository.findByUpStationId(upStation.getId()).get();

        // then
        assertThat(savedSection).isSameAs(findSection);
    }

    @Test
    @DisplayName("하행역 ID로 찾기")
    void findByDownStationId() {
        // given
        Section section = new Section(this.upStation, this.downStation, 10);
        Section savedSection = sectionRepository.save(section);

        // when
        Section findSection = sectionRepository.findByDownStationId(this.downStation.getId()).get();

        // then
        assertThat(savedSection).isSameAs(findSection);
    }

    @Test
    @DisplayName("노선 ID로 구간 목록 찾기")
    void findByLineId() {
        // given
        Section section = new Section(this.upStation, this.downStation, 10);
        Line line = new Line("2호선", "green").addSection(section);
        lineRepository.save(line);

        // when
        List<Section> sections = sectionRepository.findByLineId(section.getLine().getId());

        // then
        assertThat(sections).contains(section);
    }
}
