package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.StationTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineRepositoryTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach()
    public void setUp() {
        station1 = stationRepository.save(new Station(StationTest.STATION_NAME1));
        station2 = stationRepository.save(new Station(StationTest.STATION_NAME2));
        station3 = stationRepository.save(new Station(StationTest.STATION_NAME3));
        station4 = stationRepository.save(new Station(StationTest.STATION_NAME4));
    }

    @Test
    @DisplayName("Line Save() 후 id not null 체크")
    void save() {
        // given
        // when
        Line persistLine = lineRepository.save(LineTest.LINE1);

        // then
        assertNotNull(persistLine.getId());
    }

    @Test
    @DisplayName("같은 인스턴스 변수 값의 Line1,Line2 가 주어질때, 영속객체와 일치,불일치 검증")
    void equals() {
        // given
        Line line1 = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);
        Line line2 = new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1);

        // when
        Line persistLine = lineRepository.save(line1);

        // then
        assertAll(
            () -> assertEquals(persistLine, line1),
            () -> assertNotEquals(persistLine, line2)
        );
    }

    @Test
    @DisplayName("Line 업데이트 후 변경된 name,color 일치 체크")
    void update() {
        // given
        Line persistLine = lineRepository.save(LineTest.LINE2);

        // when
        persistLine.update(LineTest.LINE2);
        Line actual = lineRepository.findByName(persistLine.getName()).get();

        // when
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(LineTest.LINE2.getName()),
            () -> assertThat(actual.getColor()).isEqualTo(LineTest.LINE2.getColor())
        );
    }

    @Test
    @DisplayName("Line 2개 저장 후, findAll 조회시 size 2개 검증")
    void findAll() {
        // given
        lineRepository.save(new Line("10호선", "color100"));
        lineRepository.save(new Line("11호선", "color100"));

        // when
        List<Line> actual = lineRepository.findAll();

        // then
        assertThat(actual).hasSize(2);
    }


    @Test
    @DisplayName("Line 2개 저장, 1개삭제 후 findAll 조회시 size 1개 검증")
    void delete_after_findAll() {
        // given
        lineRepository.save(new Line("10호선", "color100"));
        Line deleteLine = lineRepository.save(new Line("11호선", "color100"));

        // when
        deleteLine.delete();
        List<Line> actual = lineRepository.findAll();

        // then
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("soft delete Flush 후 isDelete true(삭제됨) 반환 검증")
    void deleted() {
        // given
        Line persistLine = lineRepository.save(LineTest.LINE1);
        persistLine.delete();

        // when
        Line actual = lineRepository.findById(persistLine.getId()).get();

        // then
        assertThat(actual.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Section 추가 테스트, `Sections` 에는 null 인 `nextStation`이 1개 존재해야함")
    void addSection() {
        // given
        Line line = lineRepository.save(new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1));
        Section section = new Section(line, station1, station2, 100);

        // when
        line.addSection(section);
        Line actual = lineRepository.save(line);
        List<Section> sections = actual.getSections();

        // then
        assertThat(
            sections.stream().filter(it -> Objects.isNull(it.getNextStation())).count()).isEqualTo(
            1);
    }

    @Test
    @DisplayName("정렬된 역 목록 조회")
    void getStations() {
        // given
        Line line = lineRepository.save(new Line(LineTest.LINE_NAME1, LineTest.LINE_COLOR1));
        Section section1 = new Section(line, station1, station2, 100);
        Section section2 = new Section(line, station2, station3, 100);
        Section section3 = new Section(line, station3, station4, 100);

        // when
        노선_구간_추가_함(line, section1, section2, section3);
        Line actual = lineRepository.save(line);
        List<Station> stations = actual.getStations();

        assertThat(
            stations.stream().map(Station::getName).collect(Collectors.toList())).containsExactly(
            StationTest.STATION_NAME1, StationTest.STATION_NAME2, StationTest.STATION_NAME3,
            StationTest.STATION_NAME4);
    }

    private void 노선_구간_추가_함(Line line, Section... sections) {
        for (Section section : sections) {
            line.addSection(section);
        }
    }

}
