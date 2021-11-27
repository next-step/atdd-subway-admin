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

    public void addInitialSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(Section.of(line, upStation, downStation, distance));
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        validateDuplicatedSection(upStation, downStation);

        if (isFirstSection(downStation)) {
            addSectionWithIndex(0, Section.of(line, upStation, downStation, distance));
            return;
        }

        if (isLastSection(upStation)) {
            addSectionWithIndex(sections.size()-1, Section.of(line, upStation, downStation, distance));
            return;
        }

        Section section = getContainsSectionAtLeastOneStation(upStation, downStation);
        addAsNewMiddleSection(section, line, upStation, downStation, distance);
    }

    private void validateDuplicatedSection(Station upStation, Station downStation) {
        if (containsUpStation(upStation) && containsDownStation(downStation)) {
            throw new InvalidDuplicatedSection(upStation.getName(), downStation.getName());
        }
    }

    private boolean containsDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.isSameDownStation(downStation));
    }

    private boolean containsUpStation(Station upStation) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(upStation));
    }

    public boolean isFirstSection(Station downStation) {
        return sections.stream()
                .findFirst()
                .filter(section -> section.getUpStation().equals(downStation))
                .isPresent();
    }

    private boolean isLastSection(Station upStation) {
        return sections.stream()
                .reduce(((section, nextSection) -> nextSection))
                .filter(section -> section.isSameUpStation(upStation))
                .isPresent();
    }

    private Section getContainsSectionAtLeastOneStation(Station upStation, Station downStation) {
        return sections.stream()
                .filter(section -> section.containsStation(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new NotContainsStationException(upStation.getName(), downStation.getName()));
    }

    public void addSectionWithIndex(int index, Section section) {
        sections.add(index, section);
    }

    private void addAsNewMiddleSection(Section section, Line line, Station upStation, Station downStation, int distance) {
        section.subtractDistance(distance);
        addSectionWithIndex(sections.indexOf(section) + 1, Section.of(line, upStation, downStation, distance));
        if (section.getUpStation().equals(upStation)) {
            section.changeUpStation(downStation);
            return;
        }

        section.changeDownStation(upStation);
    }
}
