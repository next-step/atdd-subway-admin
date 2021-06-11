package nextstep.subway.section.domain;

import nextstep.subway.section.exception.ExistSameStationsException;
import nextstep.subway.section.exception.NotExistAnySameStationException;
import nextstep.subway.section.exception.NotUnderSectionDistanceException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (!CollectionUtils.isEmpty(sections)) {
            validateSection(section);
        }
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());

    }

    private void validateSection(Section newSection) {
        if (isNotValidDistance(newSection)) {
            throw new NotUnderSectionDistanceException();
        }

        if (containsUpStation(newSection) && containsDownStation(newSection)) {
            throw new ExistSameStationsException();
        }

        if (!containsUpStation(newSection) && !containsDownStation(newSection)) {
            throw new NotExistAnySameStationException();
        }
    }

    private boolean containsDownStation(Section newSection) {
        return getStations().contains(newSection.getDownStation());
    }

    private boolean containsUpStation(Section newSection) {
        return getStations().contains(newSection.getUpStation());
    }

    private boolean isNotValidDistance(Section newSection) {
        return sections.stream()
                .anyMatch(section -> (isSameUpStation(newSection, section) || isSameDownStation(newSection, section)) && section.getDistance() == newSection.getDistance());
    }

    private boolean isSameDownStation(Section newSection, Section section) {
        return section.hasSameDownStation(newSection);
    }

    private boolean isSameUpStation(Section newSection, Section section) {
        return section.hasSameUpStation(newSection);
    }
}
