package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "classpath:scripts/sectionTestData.sql")
public class SectionTest {

    // 기준 길이 값
    private static final Integer TEST_REFERENCE_DISTANCE = 10;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StationRepository stationRepository;

    private Line line;

    private Station seoulStation;
    private Station yongsanStation;

    @BeforeEach
    public void setUp() {
        seoulStation = stationRepository.findById(1L).get();
        yongsanStation = stationRepository.findById(2L).get();

        line = lineRepository.save(new Line("1호선", "blue"));
        sectionRepository.save(Section.create(Distance.valueOf(TEST_REFERENCE_DISTANCE), seoulStation, yongsanStation, line));
    }

    @Test
    @DisplayName("라인 생성시 구간 추가 성공")
    void createLineWithSection() {

        Line line = lineRepository.save(new Line("1호선-천안", "blue"));
        sectionRepository.save(Section.create(Distance.valueOf(TEST_REFERENCE_DISTANCE), seoulStation, yongsanStation, line));

        //쿼리 확인
        lineRepository.flush();

        Line findLine = lineRepository.findById(line.getId()).get();
        assertThat(findLine.getSortedStations().size()).isEqualTo(2);
        assertThat(findLine.getSortedStations().stream().map(Station::getName)).containsExactly("서울역", "용산역");
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 상행 구간(추가역, 10) 추가 성공")
    void addUpSection() {
        Station addStation = stationRepository.findById(3L).get();

        line.addLineStation(Distance.valueOf(TEST_REFERENCE_DISTANCE), addStation, seoulStation);

        assertAddSection(addStation, seoulStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 하행 구간(추가역, 10) 추가 성공")
    void addDownSection() {
        Station addStation = stationRepository.findById(3L).get();

        line.addLineStation(Distance.valueOf(TEST_REFERENCE_DISTANCE), yongsanStation, addStation);

        assertAddSection(seoulStation, yongsanStation, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 중간 구간(서울역-추가역, 5) 추가 성공")
    void addUpMiddleSection() {
        Station addStation = stationRepository.findById(3L).get();

        line.addLineStation(Distance.valueOf(5), seoulStation, addStation);

        assertAddSection(seoulStation, addStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 중간 구간(추가역-용산역, 3) 추가 성공")
    void addDownMiddleSection() {
        Station addStation = stationRepository.findById(3L).get();

        line.addLineStation(Distance.valueOf(3), addStation, yongsanStation);

        assertAddSection(seoulStation, addStation, yongsanStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 (길이가 다른) 같은 구간(서울역-용산역(길이 5)) 추가시 이미 존재한다는 BusinessException 발생")
    void addSectionAlreadyExistsFail() {

        assertThatThrownBy(
            () -> line.addLineStation(Distance.valueOf(5), yongsanStation, seoulStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.ALREADY_EXISTS_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 노선에 포함되지 않은 역만 있는 구간(강남역-역삼역(길이 10)) 추가시 BusinessException 발생")
    void addSectionNotIncludeFail() {
        Station gangnamStation = stationRepository.findById(4L).get();
        Station yeoksamStation = stationRepository.findById(5L).get();

        assertThatThrownBy(
            () -> line.addLineStation(Distance.valueOf(TEST_REFERENCE_DISTANCE), gangnamStation, yeoksamStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.NOT_INCLUDE_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 구간과 길이가 같은 중간 구간(서울역-추가역(길이 10)) 추가 시 BusinessException")
    void addSectionSameDistanceFail() {
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addLineStation(Distance.valueOf(TEST_REFERENCE_DISTANCE), seoulStation, addStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 구간보다 길이가 긴 구간(추가역-용산역(길이 12)) 추가 시 BusinessException")
    void addSectionLongDistanceFail() {
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addLineStation(Distance.valueOf(12), addStation, yongsanStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    private void assertAddSection(Station... assertStation) {
        //쿼리 확인
        lineRepository.flush();

        List<Station> sortedSections = line.getSortedStations();
        assertThat(sortedSections).containsExactly(assertStation);
    }

}