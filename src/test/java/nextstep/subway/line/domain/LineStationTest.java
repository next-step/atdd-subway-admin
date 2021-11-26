package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
public class LineStationTest {

    private static final Integer TEST_DISTANCE = 10;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    LineStationRepository lineStationRepository;

    @Autowired
    StationRepository stationRepository;

    private Line line;

    @BeforeEach
    public void setUp() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station yongsanStation = stationRepository.findById(2L).get();

        line = lineRepository.save(new Line("1호선", "blue"));

        lineStationRepository.save(
            LineStation
                .ofUpSection(seoulStation, line, Distance.valueOf(TEST_DISTANCE), yongsanStation));
        lineStationRepository.save(LineStation.fromDownSection(yongsanStation, line));
    }

    @Test
    @DisplayName("라인 생성시 구간 추가 성공")
    void createLineWithSection() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station yongsanStation = stationRepository.findById(2L).get();

        assertAll(() -> {
            assertThat(seoulStation.getName()).isEqualTo("서울역");
            assertThat(yongsanStation.getName()).isEqualTo("용산역");
        });

        Line line = lineRepository.save(new Line("1호선-천안", "blue"));

        lineStationRepository.save(
            LineStation
                .ofUpSection(seoulStation, line, Distance.valueOf(TEST_DISTANCE), yongsanStation));
        lineStationRepository.save(LineStation.fromDownSection(yongsanStation, line));

        //쿼리 확인
        lineRepository.flush();

        Line findLine = lineRepository.findById(line.getId()).get();
        assertThat(findLine.getSortedSections().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 상행 구간(추가역, 10) 추가 성공")
    void addUpSection() {

        Station seoulStation = stationRepository.findById(1L).get();
        Station addStation = stationRepository.findById(3L).get();

        line.addSection(Distance.valueOf(TEST_DISTANCE), addStation, seoulStation);

        assertAddSection(0, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 하행 구간(추가역, 10) 추가 성공")
    void addDownSection() {

        Station yongsanStation = stationRepository.findById(2L).get();
        Station addStation = stationRepository.findById(3L).get();

        line.addSection(Distance.valueOf(TEST_DISTANCE), yongsanStation, addStation);

        assertAddSection(2, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 중간 구간(서울역-추가역, 5) 추가 성공")
    void addUpMiddleSection() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station addStation = stationRepository.findById(3L).get();

        line.addSection(Distance.valueOf(TEST_DISTANCE - 5), seoulStation, addStation);

        assertAddSection(1, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 중간 구간(추가역-용산역, 3) 추가 성공")
    void addDownMiddleSection() {
        Station yongsanStation = stationRepository.findById(2L).get();
        Station addStation = stationRepository.findById(3L).get();

        line.addSection(Distance.valueOf(TEST_DISTANCE - 7), addStation, yongsanStation);

        assertAddSection(1, addStation);
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 (길이가 다른) 같은 구간(서울역-용산역(길이 5)) 추가시 이미 존재한다는 BusinessException 발생")
    void addSectionAlreadyExistsFail() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station yongsanStation = stationRepository.findById(2L).get();

        assertThatThrownBy(
            () -> line.addSection(Distance.valueOf(5), yongsanStation, seoulStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.ALREADY_EXISTS_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 노선에 포함되지 않은 역만 있는 구간(강남역-역삼역(길이 10)) 추가시 BusinessException 발생")
    void addSectionNotIncludeFail() {
        Station gangnamStation = stationRepository.findById(4L).get();
        Station yeoksamStation = stationRepository.findById(5L).get();

        assertThatThrownBy(
            () -> line.addSection(Distance.valueOf(TEST_DISTANCE), gangnamStation, yeoksamStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.NOT_INCLUDE_SECTION.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 구간과 길이가 같은 중간 구간(서울역-추가역(길이 10)) 추가 시 BusinessException")
    void addSectionSameDistanceFail() {
        Station seoulStation = stationRepository.findById(1L).get();
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addSection(Distance.valueOf(TEST_DISTANCE), seoulStation, addStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    @Test
    @DisplayName("이미 저장된 1호선(서울역-용산역)에 기존 구간보다 길이가 긴 구간(추가역-용산역(길이 12)) 추가 시 BusinessException")
    void addSectionLongDistanceFail() {
        Station yongsanStation = stationRepository.findById(2L).get();
        Station addStation = stationRepository.findById(3L).get();

        assertThatThrownBy(
            () -> line.addSection(Distance.valueOf(12), addStation, yongsanStation))
            .isInstanceOf(BusinessException.class)
            .hasMessage(Messages.LONG_OR_SAME_DISTANCE.getValues());
    }

    private void assertAddSection(int assertIndex, Station assertStation) {
        //쿼리 확인
        lineRepository.flush();

        List<LineStation> sortedLineStations = line.getSortedSections();
        assertThat(sortedLineStations.get(assertIndex).getStation().equals(assertStation)).isTrue();
    }

}