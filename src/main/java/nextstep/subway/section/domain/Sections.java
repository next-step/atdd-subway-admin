package nextstep.subway.section.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.util.CommonException;
import nextstep.subway.util.Message;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
@Getter
public class Sections {

    private static final int MINUS_ONE = -1;
    private static final int ZERO = 0;
    private static final int ONE = 1;

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
                    changeUpOrDownStation(section, oldSection, oldSection.getUpStation(), section.getUpStation());
                });
    }

    private void addUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    changeUpOrDownStation(section, oldSection, section.getDownStation(), oldSection.getDownStation());
                });
    }

    private void changeUpOrDownStation(Section section, Section oldSection, Station upOrDownStation, Station secondUpOrDownStation) {
        if (oldSection.getDistance() <= section.getDistance()) {
            CommonException.throwIllegalArgumentException(Message.DISTANCE_EXCESS_MESSAGE);
        }
        sections.add(new Section(oldSection.getLine(), upOrDownStation, secondUpOrDownStation, oldSection.getDistance() - section.getDistance()));
        sections.remove(oldSection);
    }

    private void validateNotExistStation(Section section) {
        List<Station> stations = convertSectionToStation();
        if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            CommonException.throwIllegalArgumentException(Message.NOT_EXIST_STATION_MESSAGE);
         }
    }

    private void alreadyExistStation(Section section) {
        if(convertSectionToStation().containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            CommonException.throwIllegalArgumentException(Message.ALREADY_EXIST_STATION_MESSAGE);
        }
    }

    public void removeSection(Line line, Station station) {
        List<Station> stations = convertSectionToStation();
        int index = stations.indexOf(station);
        validateBeforeRemove(index);
        if (index == ZERO) {
            stations.remove(ZERO);
        }
        if (index > ZERO) {
            Station preStation = stations.get(index - 1);
            preStation.sumDistance(station);
            stations.remove(index);
        }
        updateLastStationDistanceZero(stations);
        removeLastSection();
        convertStationToSection(line, stations);
    }

    private void validateBeforeRemove(int index) {
        if (index == MINUS_ONE) {
            CommonException.throwIllegalArgumentException(Message.NOT_FOUND_STATION_MESSAGE);
        }
        if (sections.size() <= ONE) {
            CommonException.throwIllegalArgumentException(Message.ESSENTIAL_ONE_SECTION_MESSAGE);
        }
    }

    private void updateLastStationDistanceZero(List<Station> stations) {
        Station lastStation = stations.get(stations.size() - 1);
        lastStation.updateDistance(ONE);
    }

    private void removeLastSection() {
        this.sections.remove(this.sections.size() - 1);
    }

    private List<Station> convertSectionToStation() {
        return CollectionUtils.emptyIfNull(this.sections).stream()
                .map(section -> {
                    Station upstation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    upstation.updateDistance(section.getDistance());
                    return Arrays.asList(upstation, downStation);
                })
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void convertStationToSection(Line line, List<Station> stations) {
        for (int i = ZERO; i < stations.size() - 1; i++) {
            Station upstation = stations.get(i);
            Station downStation = stations.get(i + ONE);
            int distance = upstation.getDistance();
            if (sections.size() > i) {
                Section targetSection = sections.get(i);
                targetSection.update(upstation, downStation, distance);
                continue;
            }

            sections.add(Section.builder()
                    .line(line)
                    .upStation(upstation)
                    .downStation(downStation)
                    .distance(distance)
                    .build());
        }
    }
}
