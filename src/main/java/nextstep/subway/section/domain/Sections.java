package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    private static final String STATION_NOT_CONTAINS_MESSAGE = "상행과 하행 모두 포함되지 않습니다.";
    private static final String SECTION_NOT_CORRECT_MESSAGE = "동일한 구간을 추가할 수 없습니다.";
    private static final String DISTANCE_GREATER_OR_CORRECT_MESSAGE = "거리가 작아야합니다.";
    private static final String SECTION_NOT_EXIST_MESSAGE = "구간이 1개일 때 삭제할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {

        if(validateEqualSection(section)) {
            throw new IllegalArgumentException(SECTION_NOT_CORRECT_MESSAGE);
        }

        if(validateUpStationOrDownStationNotContains(section)) {
            throw new IllegalArgumentException(STATION_NOT_CONTAINS_MESSAGE);
        }

        if(validateGreaterEqualDistance(section)) {
            throw new IllegalArgumentException(DISTANCE_GREATER_OR_CORRECT_MESSAGE);
        }

        if(!addUpSection(section) && !addDownSection(section)) {
            this.sections.add(section);
        }
    }

    private boolean addSection(Station station, Integer distance, Predicate<Section> express) {
        if(this.sections.stream().anyMatch(express)) {
            List<Section> divideBySections = this.sections.stream()
                    .filter(express)
                    .flatMap(s -> s.divideByStation(station, distance).stream())
                    .collect(toList());

            return this.sections.addAll(divideBySections);
        }
        return false;
    }

    private boolean addUpSection(Section section) {
        return addSection(section.getDownStation(), section.getDistance(), s -> s.matchUpStationFromUpStation(section));
    }

    private boolean addDownSection(Section section) {
        return addSection(section.getUpStation(), section.getDistance(), s -> s.matchDownStationFromDownStation(section));
    }

    public void remove(Station station) {

        if(this.sections.stream().noneMatch(s -> s.matchStation(station))) {
            throw new IllegalArgumentException(STATION_NOT_CONTAINS_MESSAGE);
        }

        if(this.sections.size() == 1) {
            throw new IllegalArgumentException(SECTION_NOT_EXIST_MESSAGE);
        }

        List<Section> findSections = this.sections.stream()
                .filter(s -> s.matchUpStation(station) || s.matchDownStation(station))
                .collect(toList());

        this.sections.remove(findSections);
    }

    private Section matchStation(Section section) {
        return this.sections.stream()
                .filter(s -> s.matchUpStation(section) || s.matchDownStation(section))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(STATION_NOT_CONTAINS_MESSAGE));
    }

    private boolean isGreaterEqualDistance(Section section, Section otherSection) {
        return otherSection.isGreaterOrEqualDistance(section);
    }

    private boolean isNotEmpty() {
        return !this.sections.isEmpty();
    }

    private boolean validateEqualSection(Section section) {
        return this.sections.contains(section);
    }

    private boolean validateUpStationOrDownStationNotContains(Section section) {
        return isNotEmpty() && this.sections.stream().noneMatch(s -> s.matchUpStation(section) || s.matchDownStation(section));
    }

    private boolean validateGreaterEqualDistance(Section section) {
        return isNotEmpty() && isGreaterEqualDistance(matchStation(section), section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
