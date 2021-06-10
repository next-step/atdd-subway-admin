package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

/**
 * LineRepository 기능 테스트
 */
@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LineRepository lineRepository;

    private Line greenLine;
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
        station1 = new Station("서초역");
        station2 = new Station("방배역");
        station3 = new Station("사당역");
        station4 = new Station("낙성대역");
        station5 = new Station("서울대입구역");
        station6 = new Station("봉천역");
        lineStations = Arrays.asList(station2, station3, station1, station4, station5, station6);
        stationRepository.saveAll(lineStations);
        greenLine = new Line("2호선", "green");
        section1 = new Section(station1, station2, 3, greenLine);
        section2 = new Section(station2, station3, 5, greenLine);
        section3 = new Section(station3, station4, 2, greenLine);
        section4 = new Section(station4, station5, 4, greenLine);
        sections = new Sections(Arrays.asList(section1, section2, section3, section4));
        lineRepository.save(greenLine);
    }

    @Test
    @DisplayName("구간 추가된 노선 저장")
    void new_create_line() {
        // given
        Line line = new Section(this.station1, this.station2, 10, greenLine).getLine();

        // when
        Line savedLine = lineRepository.save(line);

        // then
        assertThat(savedLine).isSameAs(line);
    }

    @Test
    @DisplayName("구간들이 포함된 기존 노선에 신규 구간 추가")
    void appendSection_to_line() {
        // given
        Line line = lineRepository.findById(greenLine.getId()).get();
        Station resultStation1 = stationRepository.findById(station1.getId()).get();
        Station resultStation2 = stationRepository.findById(station6.getId()).get();
        Sections sections = new Sections(line.getSections());
        Section section = sections.addSection(new Section(resultStation1, resultStation2, 12, line));

        // when
        Section save = sectionRepository.save(section);

        // then
        assertAll(
                () -> assertAll(
                        () -> assertThat(line.getSections().get(0).getUpStation()).isSameAs(station1),
                        () -> assertThat(line.getSections().get(0).getDownStation()).isSameAs(station2),
                        () -> assertThat(line.getSections().get(0).getDistance()).isSameAs(3)
                ),
                () -> assertAll(
                        () -> assertThat(line.getSections().get(1).getUpStation()).isSameAs(station2),
                        () -> assertThat(line.getSections().get(1).getDownStation()).isSameAs(station3),
                        () -> assertThat(line.getSections().get(1).getDistance()).isSameAs(5)
                ),
                () -> assertAll(
                        () -> assertThat(line.getSections().get(2).getUpStation()).isSameAs(station3),
                        () -> assertThat(line.getSections().get(2).getDownStation()).isSameAs(station4),
                        () -> assertThat(line.getSections().get(2).getDistance()).isSameAs(2)
                ),
                () -> assertAll(
                        () -> assertThat(line.getSections().get(3).getUpStation()).isSameAs(station6),
                        () -> assertThat(line.getSections().get(3).getDownStation()).isSameAs(station5),
                        () -> assertThat(line.getSections().get(3).getDistance()).isSameAs(2)
                ),
                () -> assertAll(
                        () -> assertThat(line.getSections().get(4).getUpStation()).isSameAs(station4),
                        () -> assertThat(line.getSections().get(4).getDownStation()).isSameAs(station6),
                        () -> assertThat(line.getSections().get(4).getDistance()).isSameAs(2)
                )
        );
    }
}
