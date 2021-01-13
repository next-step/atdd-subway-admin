package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
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

        assertThat(lineRepository.findByName("신분당선").getStations()).hasSize(2);
    }

    @Test
    @DisplayName("기존 역사이에 새로운 역을 등록")
    void saveSection1() {
        int newDistance = 0;
        int count = 0;
        List<Section> newSection = new ArrayList<>();

        Station station1 = stationRepository.save(new Station("양재역"));

        Section sectionRequest = new Section(1l, station1.getId(), 3);

        Line line = lineRepository.findByName("신분당선");
        if (line.getSections().size() == 0) {
            line.addSection(sectionRequest);
        }

        if (line.getSections().size() >= 1) {
            for (Section sectionValue: line.getSections()) {
                if (sectionValue.getUpStation() == sectionRequest.getUpStation() && sectionValue.getDownStation() != sectionRequest.getDownStation() && sectionValue.getDistance() > sectionRequest.getDistance()) {
                    newDistance = sectionValue.getDistance() - sectionRequest.getDistance();
                    Section section1 = new Section(sectionValue.getUpStation(), sectionRequest.getDownStation(), sectionRequest.getDistance());
                    Section section2 = new Section(sectionRequest.getDownStation(), sectionValue.getDownStation(), newDistance);
                    newSection.add(section1);
                    newSection.add(section2);
                }
            }

        }

        lineRepository.save(line);

        assertThat(lineRepository.findByName("신분당선").getSections()).hasSize(1);
    }
}
