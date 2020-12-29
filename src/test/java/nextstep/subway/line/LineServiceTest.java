package nextstep.subway.line;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LineBuilder;
import nextstep.subway.utils.SectionBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LineServiceTest {

    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    SectionRepository sectionRepository;

    @AfterEach
    public void cleanup() {
        sectionRepository.deleteAllInBatch();
        lineRepository.deleteAllInBatch();
        stationRepository.deleteAllInBatch();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void betweenStationRegister() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Line line = saveLine("신분당선", "bg-red-300", 강남역, 판교역, 10);

        addSection(line, 양재역, 판교역,6);

        assertThat(getStationResponses(line))
                .extracting("name")
                .containsExactly("강남역", "판교역", "양재역");
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void sameUpStationRegister() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Line line = saveLine("신분당선", "bg-red-300", 강남역, 양재역, 10);

        addSection(line, 강남역, 판교역, 6);

        assertThat(getStationResponses(line))
                .extracting("name")
                .containsExactly("판교역", "강남역", "양재역");
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void sameDownStationRegister() {
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 양재시민의숲 = stationRepository.save(new Station("양재시민의숲"));

        Line line = saveLine("신분당선", "bg-red-300", 강남역, 판교역, 10);

        addSection(line, 양재역, 판교역, 6);

        addSection(line, 양재시민의숲, 양재역, 6);

        assertThat(getStationResponses(line))
                .extracting("name")
                .containsExactly("강남역", "판교역", "양재역", "양재시민의숲");
    }

    private Line saveLine(String lineName, String lineColor, Station upStation, Station downStation, int distance) {
        return lineRepository.save(new LineBuilder()
                .withName(lineName)
                .withColor(lineColor)
                .withSection(new SectionBuilder()
                        .withUpStation(upStation)
                        .withDownStation(downStation)
                        .withDistance(distance)
                        .build())
                .build());
    }

    private void addSection(Line line, Station upStation, Station downStation, int distance) {
        lineService.addSection(line.getId(), new SectionBuilder()
                .withUpStation(downStation)
                .withDownStation(upStation)
                .withDistance(distance).toSectionRequest());
    }

    private List<StationResponse> getStationResponses(Line line) {
        return lineService.findLineById(line.getId()).getStations();
    }
}
