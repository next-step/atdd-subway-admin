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
        final Station station1 = new Station("청계산 입구");
        station1.setLineStation(line);
        stationRepository.save(station1);
        final Station station2 = new Station("상현");
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
        Station station1 = stationRepository.save(new Station("판교"));
        SectionRequest sectionRequest = new SectionRequest(stationRepository.findByName("청계산 입구").getId(), station1.getId(), 3);
        final LineService lineService = new LineService(lineRepository, sectionRepository, stationRepository);
        LineResponse response = lineService.saveSection(lineId, sectionRequest);

        assertThat(response.getSections()).hasSize(2);
        assertThat(stationRepository.findById(response.getSections().get(0).getUpStation()).get().getName()).isEqualTo("청계산 입구");
        assertThat(stationRepository.findById(response.getSections().get(1).getUpStation()).get().getName()).isEqualTo("판교");
    }

    @Test
    @DisplayName("새로운 역의 하행을 기존 노선 상행역으로 등록")
    void saveSection2() {
        Long lineId = lineRepository.findByName("신분당선").getId();
        Station station1 = stationRepository.save(new Station("양재역"));
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), stationRepository.findByName("청계산 입구").getId(), 3);
        final LineService lineService = new LineService(lineRepository, sectionRepository, stationRepository);
        LineResponse response = lineService.saveSection(lineId, sectionRequest);

        assertThat(response.getSections()).hasSize(2);
        assertThat(stationRepository.findById(response.getSections().get(0).getUpStation()).get().getName()).isEqualTo("양재역");
        assertThat(stationRepository.findById(response.getSections().get(0).getDownStation()).get().getName()).isEqualTo("청계산 입구");
        assertThat(response.getSections().get(0).getDistance()).isEqualTo(3);
        assertThat(stationRepository.findById(response.getSections().get(1).getUpStation()).get().getName()).isEqualTo("청계산 입구");
        assertThat(stationRepository.findById(response.getSections().get(1).getDownStation()).get().getName()).isEqualTo("상현");
        assertThat(response.getSections().get(1).getDistance()).isEqualTo(15);
    }
}
