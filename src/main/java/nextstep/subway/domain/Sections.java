package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.ErrorMessage.DUPLICATE_SECTION;
import static nextstep.subway.common.ErrorMessage.NOT_ALLOW_ADD_SECTION;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public boolean hasStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalsUpStation(station) || section.equalsDownStation(station));
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
}
