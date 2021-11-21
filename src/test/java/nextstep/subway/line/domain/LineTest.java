package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class LineTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @AfterEach
    void tearDown() {
        stationRepository.deleteAll();
        lineRepository.deleteAll();
    }

    @Test
    void addSection() {
        // given
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));

        // when
        line.addSection(new Section(송내역, 신도림역, 10));

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    @DisplayName("서로 연결이 불가능한 구간이 추가되려고 할 때 예외 발생")
    void addSectionByImpossibleConnection() {
        // given
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 의정부역 = stationRepository.save(new Station("의정부역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Station 강남역 = stationRepository.save(new Station("강남역"));
        line.addSection(new Section(송내역, 의정부역, 10));

        // when, then
        assertThatThrownBy(() ->
            line.addSection(new Section(신도림역, 강남역, 10))
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("상행 - 하행 순으로 정렬된 지하철역 목록을 반환")
    void computeSortedStations() {
        // given
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 동인천역 = stationRepository.save(new Station("동인천역"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Station 의정부역 = stationRepository.save(new Station("의정부역"));
        line.addSection(new Section(동인천역, 송내역, 10));
        line.addSection(new Section(송내역, 신도림역, 10));
        line.addSection(new Section(신도림역, 의정부역, 10));

        // when
        final List<Station> actualStations = line.computeSortedStations();

        //then
        assertThat(actualStations).containsExactly(동인천역, 송내역, 신도림역, 의정부역);
    }
}
