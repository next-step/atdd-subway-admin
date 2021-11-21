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
        final Station 부천역 = stationRepository.save(new Station("부천역"));
        final Station 용산역 = stationRepository.save(new Station("용산역"));

        // when
        line.addSection(new Section(송내역, 신도림역, 10));
        line.addSection(new Section(부천역, 신도림역, 5));
        line.addSection(new Section(신도림역, 용산역, 5));

        // then
        assertThat(line.computeSortedStations()).containsExactly(송내역, 부천역, 신도림역, 용산역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외 발생")
    @Test
    void addSectionWithLongDistance() {
        // givevn
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 의정부역 = stationRepository.save(new Station("의정부역"));
        final Station 부천역 = stationRepository.save(new Station("부천역"));
        line.addSection(new Section(송내역, 의정부역, 10));

        // when, then
        assertThatThrownBy(() -> line.addSection(new Section(부천역, 의정부역, 10)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("추가되는 구간의 길이가 기존 역 사이의 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 예외 발생")
    @Test
    void addSectionThatAlreadyExists() {
        // givevn
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 의정부역 = stationRepository.save(new Station("의정부역"));
        line.addSection(new Section(송내역, 의정부역, 10));

        // when, then
        assertThatThrownBy(() -> line.addSection(new Section(송내역, 의정부역, 5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("서로 연결이 불가능한 구간이 추가되려고 할 때 예외 발생")
    @Test
    void addSectionNotConnected() {
        // given
        final Line line = lineRepository.save(new Line("1호선", "indigo"));
        final Station 송내역 = stationRepository.save(new Station("송내역"));
        final Station 의정부역 = stationRepository.save(new Station("의정부역"));
        final Station 신도림역 = stationRepository.save(new Station("신도림역"));
        final Station 강남역 = stationRepository.save(new Station("강남역"));
        line.addSection(new Section(송내역, 의정부역, 10));

        // when, then
        assertThatThrownBy(() -> line.addSection(new Section(신도림역, 강남역, 5)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("추가되는 구간은 기존의 구간과 연결 가능하여야 합니다.");
    }

    @DisplayName("상행 - 하행 순으로 정렬된 지하철역 목록을 반환")
    @Test
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
