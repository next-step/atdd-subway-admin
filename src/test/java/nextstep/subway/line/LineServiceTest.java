package nextstep.subway.line;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LineServiceTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        final Line line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        final Station station1 = new Station("강남역");
        station1.setLineStation(line);
        stationRepository.save(station1);
        final Station station2 = new Station("청계산 입구");
        station2.setLineStation(line);
        stationRepository.save(station2);
        Section section = new Section(station1.getId(), station2.getId(), 15);
        line.addSection(sectionRepository.save(section));
        //line.addStation(station1);
        //line.addStation(station2);
        //lineRepository.save(line);

        assertThat(lineRepository.findByName("신분당선").getSections()).hasSize(1);
    }

    @Test
    @DisplayName("기존 역사이에 새로운 역을 등록")
    void saveSection1() {

        Long lineId = lineRepository.findByName("신분당선").getId();
        Station station1 = stationRepository.save(new Station("양재역"));
        SectionRequest sectionRequest = new SectionRequest(stationRepository.findByName("강남역").getId(), station1.getId(), 3);
        final LineService lineService = new LineService(lineRepository, stationRepository);
        LineResponse response = lineService.saveSection(lineId, sectionRequest);

        assertThat(response.getSections()).hasSize(2);
    }
}
