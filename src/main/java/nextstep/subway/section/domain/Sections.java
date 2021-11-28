package nextstep.subway.section.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        return this.sections.stream()
                .sorted()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void updateSection(Section newSection) {
        checkValidSection(newSection);
        addSection(newSection);
    }

    public void addSection(Section newSection){
        for (Section section : sections) {
            section.addInnerSection(newSection);
        }
        sections.add(newSection);
    }

    private void checkValidSection(Section section) {
        if (isExist(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_SECTION_ALREADY_EXISTS);
        }
        if (registerBothStationInLine(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_STATIONS_ALREADY_EXISTS);
        }
        if (notSearchBothStationInLine(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION);
        }
    }

    private boolean registerBothStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .filter(it -> it.equals(section.getUpStation()) || it.equals(section.getDownStation()))
                .collect(Collectors.toList()).size() == 2;
    }

    private boolean notSearchBothStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .filter(it -> it.equals(section.getUpStation()) || it.equals(section.getDownStation()))
                .collect(Collectors.toList()).isEmpty();
    }

    private boolean isExist(Section section) {
        return this.sections.stream()
                .anyMatch(it -> isSameSection(section, it));
    }

    private boolean isSameSection(Section section, Section it) {
        return it.equals(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
