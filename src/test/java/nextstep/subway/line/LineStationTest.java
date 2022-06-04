package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class LineStationTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    private Station 강남역;
    private Station 양재역;
    private Line 신분당선_강남_양재_10L;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        신분당선_강남_양재_10L = new Line("신분당선", "bg-red-600", 강남역, 양재역, new Distance(10L));
    }

    @DisplayName("line, station, preStation, distance 를 가진다.")
    @Test
    void createTest() {
        assertThat(new LineStation(신분당선_강남_양재_10L, new Section(강남역, 양재역, new Distance(10L))))
                .isEqualTo(new LineStation(신분당선_강남_양재_10L, new Section(강남역, 양재역, new Distance(10L))));
    }

    @DisplayName("동등성 비교")
    @Test
    void identityTest() {
        LineStation savedLineStation = lineStationRepository.save(new LineStation(신분당선_강남_양재_10L, new Section(강남역, 양재역, new Distance(10L))));
        AssertionsForClassTypes.assertThat(lineStationRepository.findById(savedLineStation.getId()).orElseThrow(EntityNotFoundException::new)).isEqualTo(savedLineStation);
    }

    @DisplayName("연관관계 테스트")
    @Test
    void mappingTest() {
        LineStation savedLineStation = lineStationRepository.save(new LineStation(신분당선_강남_양재_10L, new Section(강남역, 양재역, new Distance(10L))));
        Line line = lineRepository.findById(savedLineStation.getLine().getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(line.getLineStations().contains(savedLineStation)).isTrue();
    }
}
