package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    private static final String STATION_NOT_CONTAINS_MESSAGE = "상행과 하행 모두 포함되지 않습니다.";
    private static final String SECTION_NOT_CORRECT_MESSAGE = "동일한 구간을 추가할 수 없습니다.";
    private static final String DISTANCE_GREATER_OR_CORRECT_MESSAGE = "거리가 작아야합니다.";
    private static final String SECTION_NOT_EXIST_MESSAGE = "구간이 1개일 때 삭제할 수 없습니다.";
    private static final int MIN_SECTION_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @BatchSize(size = 200)
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

    public boolean remove(Station station) {

        if(validateNotContains(station)) {
            throw new IllegalArgumentException(STATION_NOT_CONTAINS_MESSAGE);
        }

        if(validateIfRemoveNotExist()) {
            throw new IllegalArgumentException(SECTION_NOT_EXIST_MESSAGE);
        }

        addSectionIf(station);
        return removeSection(station);
    }

    private boolean addUpSection(Section section) {
        return addSection(section.getDownStation(), section.getDistance(), s -> s.matchUpStationFromUpStation(section));
    }

    private boolean addDownSection(Section section) {
        return addSection(section.getUpStation(), section.getDistance(), s -> s.matchDownStationFromDownStation(section));
    }

    private boolean addSection(Station station, Integer distance, Predicate<Section> express) {
            List<Section> divideBySections = this.sections.stream()
                    .filter(express)
                    .flatMap(s -> s.divideByStation(station, distance).stream())
                    .collect(toList());

            return this.sections.addAll(divideBySections);
    }

    private void addSectionIf(Station station) {
        List<Section> sections = findSections(s -> s.matchAnyStation(station));
        if(sections.size() >= 2) {
            sections.stream()
                    .reduce((a, b) -> a.addBySection(b))
                    .ifPresent(s -> this.sections.add(s));
        }
    }
    private List<Section> findSections(Predicate<Section> express) {
        return this.sections.stream()
                .filter(express)
                .collect(toList());
    }

    private boolean removeSection(Station station) {
        return this.sections.removeIf(s -> s.matchAnyStation(station));
    }

    private Optional<Section> matchStation(Section section) {
        return this.sections.stream()
                .filter(s -> s.matchUpStationFromUpStation(section) || s.matchDownStationFromDownStation(section))
                .findFirst();
    }

    private boolean validateNotContains(Station station) {
        return this.sections.stream().noneMatch(s -> s.matchStation(station));
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
        return matchStation(section)
                .map(s -> isNotEmpty() && isGreaterEqualDistance(s, section))
                .orElse(false);
    }

    private boolean validateIfRemoveNotExist() {
        return this.sections.size() == MIN_SECTION_COUNT;
    }

    private Stream<Section> sectionStream() {
        return this.sections.stream();
    }

    public List<Section> getSections() {
        return sections;
    }
}
