package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.CannotAddException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:scripts/sectionTestData.sql")
public class SectionTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StationRepository stationRepository;

    private Line line;

    private Station seoulStation;
    private Station yongsanStation;

    @Test
    @DisplayName("라인 생성시 구간 추가 성공")
    void createLineWithSection() {
        seoulStation = stationRepository.findById(1L).get();
        yongsanStation = stationRepository.findById(2L).get();

        Line line = lineRepository.save(new Line("1호선-천안", "blue"));
        Section.create(Distance.valueOf(10), seoulStation, yongsanStation, line);

        //쿼리 확인
        lineRepository.flush();

        List<Station> stations = lineRepository.findById(line.getId()).get()
            .getSortedStations();

        assertAll(() -> {
            assertThat(stations.size()).isEqualTo(2);
            assertThat(stations).extracting(Station::getName).containsExactly("서울역", "용산역");
        });
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 상행 구간(추가역, 10) 추가 성공")
    void addUpSection() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        line.addSections(Distance.valueOf(10), addStation, seoulStation);

        assertAddSection(addStation, seoulStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 하행 구간(추가역, 10) 추가 성공")
    void addDownSection() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        line.addSections(Distance.valueOf(10), yongsanStation, addStation);

        assertAddSection(seoulStation, yongsanStation, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 중간 구간(서울역-추가역, 5) 추가 성공")
    void addUpMiddleSection() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        line.addSections(Distance.valueOf(5), seoulStation, addStation);

        assertAddSection(seoulStation, addStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 중간 구간(추가역-용산역, 길이 3) 추가 성공")
    void addDownMiddleSection() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        line.addSections(Distance.valueOf(3), addStation, yongsanStation);

        assertAddSection(seoulStation, addStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 (길이가 다른) 같은 구간(서울역-용산역(길이 5)) 추가시 이미 존재한다는 CannotAddException 발생")
    void addSectionAlreadyExistsFail() {
        addLine1WithSeoulToYongsanLength10();

        assertThatThrownBy(
            () -> line.addSections(Distance.valueOf(5), yongsanStation, seoulStation))
            .isInstanceOf(CannotAddException.class)
            .hasMessage(Messages.ALREADY_EXISTS_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 노선에 포함되지 않은 역만 있는 구간(강남역-역삼역) 추가시 포함된 구간이 없다는 CannotAddException 발생")
    void addSectionNotIncludeFail() {
        addLine1WithSeoulToYongsanLength10();
        Station gangnamStation = stationRepository.findById(4L).get();
        Station yeoksamStation = stationRepository.findById(5L).get();

        assertThatThrownBy(
            () -> line.addSections(Distance.valueOf(10), gangnamStation, yeoksamStation))
            .isInstanceOf(CannotAddException.class)
            .hasMessage(Messages.NOT_INCLUDE_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 기존 구간과 길이가 같은 중간 구간(서울역-추가역(길이 10)) 추가 시 구간의 길이는 기존 구간보다 작아야 한다는  CannotAddException")
    void addSectionSameDistanceFail() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addSections(Distance.valueOf(10), seoulStation, addStation))
            .isInstanceOf(CannotAddException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    @Test
    @DisplayName("1호선(서울역-추가역-용산역, 5-5)에서 추가역 삭제 (서울역-용산역, 10) 성공")
    void deleteSection() {
        //given
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();
        line.addSections(Distance.valueOf(5), addStation, yongsanStation);

        //when
        line.deleteSection(addStation);

        //then
        assertThat(line.getSortedStations()).extracting(Station::getName).containsExactly("서울역", "용산역");

    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역, 길이 10)에 기존 구간보다 길이가 긴 구간(추가역-용산역(길이 12)) 추가 시 기존 구간보다 작아야 한다는 CannotAddException")
    void addSectionLongDistanceFail() {
        addLine1WithSeoulToYongsanLength10();
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addSections(Distance.valueOf(12), addStation, yongsanStation))
            .isInstanceOf(CannotAddException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    private void assertAddSection(Station... assertStation) {
        //쿼리 확인
        lineRepository.flush();

        List<Station> sortedSections = line.getSortedStations();
        assertThat(sortedSections).containsExactly(assertStation);
    }

    private void addLine1WithSeoulToYongsanLength10() {
        seoulStation = stationRepository.findById(1L).get();
        yongsanStation = stationRepository.findById(2L).get();

        line = lineRepository.save(new Line("1호선", "blue"));
        sectionRepository.save(Section.create(Distance.valueOf(10), seoulStation, yongsanStation, line));
    }

}