package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    private Station newStation;
    private List<Station> stations;
    private Line greenLine;

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private List<Station> lineStations;
    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;
    private Sections sections;

    @BeforeEach
    void setUp() {
        upStation = new Station("서울역");
        downStation = new Station("삼각지역");
        newStation = new Station("숙대입구역");
        greenLine = new Line("4호선", "blue");
        lineRepository.save(greenLine);
        stations = stationRepository.saveAll(Arrays.asList(upStation, downStation, newStation));

        station1 = new Station("서초역");
        station2 = new Station("방배역");
        station3 = new Station("사당역");
        station4 = new Station("낙성대역");
        station5 = new Station("서울대입구역");
        station6 = new Station("봉천역");
        lineStations = Arrays.asList(station2, station3, station1, station4, station5, station6);
        stationRepository.saveAll(lineStations);
        line = new Line("2호선", "green");
        section1 = new Section(station1, station2, 3, line);
        section2 = new Section(station2, station3, 5, line);
        section3 = new Section(station3, station4, 2, line);
        section4 = new Section(station4, station5, 4, line);
        sections = new Sections(Arrays.asList(section1, section2, section3, section4));
        lineRepository.save(line);
    }

    @Test
    @DisplayName("상행역 ID로 찾기")
    void findByUpStationId() {
        // given
        Section section = new Section(this.upStation, this.downStation, 10, greenLine);
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
        Section section = new Section(this.upStation, this.downStation, 10, this.greenLine);
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
        Section section = new Section(this.upStation, this.downStation, 10, this.greenLine);
        sectionRepository.save(section);

        // when
        List<Section> sections = sectionRepository.findByLineId(section.getLine().getId());

        // then
        assertThat(sections).contains(section);
    }

    @Test
    @DisplayName("구간 추가 등록")
    void save_newSection() {
        // given
        Sections sections = new Sections(sectionRepository.findByLineId(line.getId()));
        Section section = new Section(station1, station6, 9, line);
        Section appendedSection = sections.addSection(section);

        // when
        Section savedSection = sectionRepository.save(appendedSection);

        // then
        assertAll(
                () -> assertThat(savedSection.getUpStation()).isSameAs(station3),
                () -> assertThat(savedSection.getDownStation()).isSameAs(station6),
                () -> assertThat(savedSection.getDistance().isEqualTo(1)).isTrue()
        );
    }
}
