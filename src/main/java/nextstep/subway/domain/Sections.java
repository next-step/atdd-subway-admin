package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedSectionException;
import nextstep.subway.exception.InvalidDistanceException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        if (this.sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateUnique(newSection);
        validateDistance(newSection);
        updateSection(newSection);
        sections.add(newSection);
    }

    private void updateSection(Section newSection) {
        this.sections.forEach(section -> {
            if (section.matchUpStation(newSection)) {
                section.updateUpStationAndDistance(newSection);
                return;
            }

            if (section.matchDownStation(newSection)) {
                section.updateDownStationAndDistance(newSection);
                return;
            }
        });
    }

    private void validateUnique(Section newSection) {
        if (sections.contains(newSection)) {
            throw new DuplicatedSectionException(newSection.getUpStation().getId(), newSection.getDownStation().getId());
        }
    }

    public boolean isNotEmpty() {
        return !this.sections.isEmpty();
    }

    public List<Station> findStations() {
        return this.sections.stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void validateDistance(Section newSection) {
        boolean validDistance = this.sections.stream()
            .anyMatch(section -> {
                if (!section.isBetweenStation(newSection)) {
                    return true;
                }

                return newSection.isShort(section);
            });

        if (!validDistance) {
            throw new InvalidDistanceException(newSection.getDistance());
        }
    }
}
