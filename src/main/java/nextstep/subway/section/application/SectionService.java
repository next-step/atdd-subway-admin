package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class SectionService {
    private SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void saveSection(int distance, Station upStation, Station station, Line line) {
        Section section = new Section(distance, upStation, station, line);
        sectionRepository.save(section);
    }

    public List<Station> getStations(Line line) {
        List<Section> sections = sectionRepository.findByLine(line);
        Optional<Section> firstSection = sections.stream()
                .filter(section -> section.getUpStation() == null)
                .findFirst();
        List<Station> stations = new ArrayList<>();


        while(firstSection.isPresent()) {
            Section nowSection = firstSection.get();
            stations.add(nowSection.getStation());
            firstSection = sections.stream()
                    .filter(section -> section.getUpStation() == nowSection.getStation())
                    .findFirst();
        }
        return stations;
    }
}
