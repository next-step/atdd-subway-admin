package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addToSections(Line line, Station upStation, Station downStation, int distance) {
        sections.add(Section.of(line, upStation, downStation, distance));
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation);

        changeUpSectionIfExist(upStation, downStation, distance);
        changeDownSectionIfExist(downStation, upStation, distance);

        addToSections(line, upStation, downStation, distance);
    }

    private void validateSection(Station upStation, Station downStation) {
        validateDuplicateSection(upStation, downStation);
        notContainsStationException(upStation, downStation);
    }

    private void changeUpSectionIfExist(Station station, Station changeStation, int distance) {
        sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .ifPresent(section -> section.changeUpStation(changeStation, distance));
    }

    private void changeDownSectionIfExist(Station station, Station changeStation, int distance) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .ifPresent(section -> section.changeDownStation(changeStation, distance));
    }

    private void validateDuplicateSection(Station upStation, Station downStation) {
        if (containsUpStation(upStation) && containsDownStation(downStation)) {
            throw new InvalidDuplicatedSection(upStation.getName(), downStation.getName());
        }
    }

    private void notContainsStationException(Station upStation, Station downStation) {
        if (!containsUpStation(upStation) &&
            !containsUpStation(downStation) &&
            !containsDownStation(upStation) &&
            !containsDownStation(downStation)) {
            throw new NotContainsStationException(upStation.getName(), downStation.getName());
        }
    }

    private boolean containsUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(station));
    }

    private boolean containsDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameDownStation(station));
    }

}
