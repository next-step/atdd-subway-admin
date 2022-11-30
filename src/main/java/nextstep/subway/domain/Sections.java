package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @ReadOnlyProperty
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        checkValidation(section);
        if (isSameUpStation(section)) {
            updateStationWhenUpStationSame(section);
        }
        if (isSameDownStation(section)) {
            updateStationWhenDownStationSame(section);
        }
        sections.add(section);
    }

    private boolean hasEndPointIssue(Section section) {
        Station endUpStation = findEndUpStation(sections.get(sections.size()-1));
        return true; // TODO
    }

    private Station findEndUpStation(Section section) {
        Optional<Station> endUpStation = hasNotDownStation(section.getUpStation();
        return null;
    }

    private Optional<Section> hasNotDownStation(Station station) {
        return sections.stream().filter(it -> it.isSameDownStationId(station)).findFirst();
    }

    private void updateStationWhenDownStationSame(Section section) {
        Optional<Section> findSection = sections.stream().filter(it -> it.isSameDownStationId(section)).findFirst();
        findSection.get().updateAndCreateTwiceSectionWhenDownStationSame(section);
    }

    private void updateStationWhenUpStationSame(Section section) {
        Optional<Section> findSection = sections.stream().filter(it -> it.isSameUpStationId(section)).findFirst();
        findSection.get().updateAndCreateTwiceSectionWhenUpStationSame(section);
    }

    private boolean isSameUpStation(Section section) {
        return sections.stream()
                .filter(it -> it.isSameUpStationId(section))
                .findFirst().isPresent();
    }

    private boolean isSameDownStation(Section section) {
        return sections.stream()
                .filter(it -> it.isSameDownStationId(section))
                .findFirst().isPresent();
    }

    private void checkValidation(Section section) {
        if (sections.stream().anyMatch(s -> s.equals(section))) {
            throw new IllegalArgumentException(ErrorCode.NO_SAME_SECTION_EXCEPTION.getErrorMessage());
        }
        if (this.sections.size() > 0 && isNoMatchStation(section)) {
            throw new IllegalArgumentException(ErrorCode.NO_MATCH_STATION_EXCEPTION.getErrorMessage());
        }
        if (hasEndPointIssue(section)) {
            throw new IllegalArgumentException(ErrorCode.ADD_END_POINT_ISSUE.getErrorMessage());
        }
    }

    private boolean isNoMatchStation(Section newSection) {
        return sections.stream()
                .map(Section::toStations)
                .flatMap(Collection::stream)
                .distinct()
                .noneMatch(station -> newSection.isSameDownStationId(station) ||
                        newSection.isSameUpStationId(station));
    }

    public List<Section> asList() {
        return Collections.unmodifiableList(sections);
    }
}
