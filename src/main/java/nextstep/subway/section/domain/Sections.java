package nextstep.subway.section.domain;

import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.util.CommonException;
import nextstep.subway.util.Message;

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
        validateNotExistStation(section);
        alreadyExistStation(section);

        addUpStation(section);
        addDownStation(section);

        this.sections.add(section);
    }

    private void addDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getDownStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    selectWithStation(section, oldSection, oldSection.getUpStation(), section.getUpStation());
                });
    }

    private void addUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    selectWithStation(section, oldSection, section.getDownStation(), oldSection.getDownStation());
                });
    }

    private void selectWithStation(Section section, Section oldSection, Station upStation, Station upStation2) {
        if (oldSection.getDistance() <= section.getDistance()) {
            CommonException.IllegalArgumentException(Message.DISTANCE_EXCESS_MESSAGE);
        }
        sections.add(new Section(oldSection.getLine(), upStation, upStation2, oldSection.getDistance() - section.getDistance()));
        sections.remove(oldSection);
    }

    private void validateNotExistStation(Section section) {
        if(!getStations().contains(section.getUpStation()) && !getStations().contains(section.getDownStation())) {
            CommonException.IllegalArgumentException(Message.NOT_EXIST_STATION_MESSAGE);
         }
    }

    private void alreadyExistStation(Section section) {
        if(getStations().containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            CommonException.IllegalArgumentException(Message.ALREADY_EXIST_STATION_MESSAGE);
        }
    }
}
