package nextstep.subway.section.domain;

import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
@NoArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(Line line, Station upStation, Station downStation, int distance) {
        sections.add(Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build());
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());
        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }
        return stations;
    }
    private Section findUpEndSection() {
        List<Station> downStations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(toList());
        return this.sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }

    private Section findSectionByNextUpStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }

    public void add(Section section) {
        // 역이 둘다 존재 시
        List<Station> stations = getStations();
        if(stations.containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            throw new IllegalArgumentException();
        }

        // 둘다 존재 하지 않을
       if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException();
        }

        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    if (oldSection.getDistance() <= section.getDistance()) {
                        throw new IllegalArgumentException();
                    }
                    sections.add(new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });

        sections.stream()
                .filter(oldSection -> section.getDownStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    if (oldSection.getDistance() <= section.getDistance()) {
                        throw new IllegalArgumentException();
                    }
                    sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
        this.sections.add(section);
    }
}
