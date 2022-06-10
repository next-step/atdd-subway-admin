package nextstep.subway.line;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineStationsEntityTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    LineStationRepository lineStationRepository;

    @Autowired
    StationRepository stationRepository;

    private Station 강남역;
    private Station 양재역;

    private Line savedLine;
    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        savedLine = lineRepository.save(new Line("신분당선", "bg-red-600"));
    }

    @DisplayName("LineStations 에서 삭제되면 Line 에서도 검색이 안되어 한다.")
    @Test
    void noSearchTestWhenRemoveLineStationInLineStations() {
        LineStation lineStation = new LineStation(savedLine, 강남역);
        LineStations lineStations = new LineStations(Arrays.asList(lineStation, new LineStation(savedLine, new Section(강남역, 양재역, 10))));
        lineStations.removeLineStationBy(lineStation) ;
        assertThat(savedLine.getLineStations().getStations().contains(강남역)).isFalse();
        assertThat(lineStations.getStations().contains(강남역)).isFalse();
    }
}
