package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static nextstep.subway.common.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void updateSection(Station upStation, Station downStation, int distance) {
        this.validateDuplicateSections(upStation, downStation);
        this.validateUpdatable(upStation, downStation);

        Optional<Section> sectionOptional = sections.stream()
                .filter(section -> section.equalsUpStation(upStation) || section.equalsDownStation(downStation))
                .findAny();

        if (!sectionOptional.isPresent()) {
            return;
        }

        Section section = sectionOptional.get();
        section.minusDistance(distance);

        if (section.equalsUpStation(upStation)) {
            section.updateUpStation(downStation);
            return;
        }

        section.updateDownStation(upStation);
    }

    private void validateDuplicateSections(Station upStation, Station downStation) {
        Optional<Section> findUpStation = sections.stream()
                .filter(section -> section.contains(upStation))
                .findAny();

        Optional<Section> findDownStation = sections.stream()
                .filter(section -> section.contains(downStation))
                .findAny();

        if (findUpStation.isPresent() && findDownStation.isPresent()) {
            throw new IllegalArgumentException(DUPLICATE_SECTION.getMessage());
        }
    }

    private void validateUpdatable(Station upStation, Station downStation) {
        sections.stream()
                .filter(section -> isEqualsStation(upStation, downStation, section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_ALLOW_ADD_SECTION.getMessage()));
    }

    private boolean isEqualsStation(Station upStation, Station downStation, Section section) {
        return section.equalsUpStation(downStation)
                || section.equalsUpStation(upStation)
                || section.equalsDownStation(upStation)
                || section.equalsDownStation(downStation);
    }

    public List<Section> values() {
        return Collections.unmodifiableList(sections);
    }

    public void delete(Station station) {
        validateHasSection(station);
        validateSingleSection();

        List<Section> targets = sections.stream()
                .filter(section -> section.contains(station))
                .collect(toList());

        if (targets.size() == 1) {
            targets.forEach(sections::remove);
        }
    }

    private void validateHasSection(Station station) {
        if (!this.hasStation(station)) {
            throw new IllegalArgumentException(CANNOT_REMOVE_STATION_NOT_INCLUDE_LINE.getMessage());
        }
    }

    private boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    private void validateSingleSection() {
        if (this.isSingleSection()) {
            throw new IllegalArgumentException(CANNOT_REMOVE_STATION_ONLY_ONE_SECTION.getMessage());
        }
    }

    private boolean isSingleSection() {
        return this.sections.size() == 1;
    }
}
