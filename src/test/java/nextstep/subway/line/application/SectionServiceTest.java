package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SectionServiceTest {
    private Station A역;
    private Station B역;
    private Station C역;
    private Line line;

    @Autowired
    SectionService sectionService;

    @Autowired
    LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        C역 = stationRepository.save(new Station("C역"));
        A역 = stationRepository.save(new Station("A역"));
        B역 = stationRepository.save(new Station("B역"));
        line = createLineWithUpStationAndDownStation(A역,C역);
    }

    @AfterEach
    void cleanup() {
        sectionRepository.deleteAllInBatch();
        lineRepository.deleteAllInBatch();
        stationRepository.deleteAllInBatch();
    }

    private Line createLineWithUpStationAndDownStation(Station upStation, Station downStaion) {
        Line line = new Line("2호선", "green");

        Section section = Section.builder().upStation(upStation)
                .downStation(downStaion)
                .line(line)
                .distance(50)
                .build();

        line.addSection(section);
        return lineRepository.save(line);
    }

    @DisplayName("[구간등록] 역 사이에 새로운 역을 등록")
    @Test
    void addSection() {
        // when
        sectionService.addSection(line.getId(),new SectionRequest(A역.getId(), B역.getId(), 10));

        // then
        LineResponse actual = lineService.findById(line.getId());
        outputStationName(actual.getStationsResponses());
        assertThat(actual.getStationsResponses().size()).isEqualTo(3);
    }

    @DisplayName("[구간등록] 새로운 상행 종점 등록")
    @Test
    void addSection2() {
        // when
        sectionService.addSection(line.getId(),new SectionRequest(B역.getId(), A역.getId(), 10));

        // then
        LineResponse actual = lineService.findById(line.getId());
        outputStationName(actual.getStationsResponses());
        assertThat(actual.getStationsResponses().size()).isEqualTo(3);
    }

    @DisplayName("[구간등록] 새로운 하행 종점 등록")
    @Test
    void addSection3() {
        // when
        sectionService.addSection(line.getId(),new SectionRequest(C역.getId(), B역.getId(), 10));

        // then
        LineResponse actual = lineService.findById(line.getId());
        outputStationName(actual.getStationsResponses());
        assertThat(actual.getStationsResponses().size()).isEqualTo(3);
    }

    private void outputStationName(List<StationResponse> stationResponses) {
        for (StationResponse stationResponse : stationResponses) {
            System.out.println(stationResponse.getName());
        }
    }

    @DisplayName("[구간삭제] 역사이 구간 역 삭제")
    @Test
    void remove() {
        // given
        sectionService.addSection(line.getId(),new SectionRequest(C역.getId(), B역.getId(), 10));

        // when
        sectionService.removeSection(line.getId(),C역.getId());

        // then
        LineResponse actual = lineService.findById(line.getId());
        outputStationName(actual.getStationsResponses());
        assertThat(actual.getStationsResponses().size()).isEqualTo(2);
    }
}