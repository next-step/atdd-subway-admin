package nextstep.subway.section.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Section add(Section section) {
        if (sections.size() > 0) {
            checkValidateSection(section);
            adjustSections(section);
        }

        sections.add(section);
        return section;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .sorted(Comparator.comparingLong(Station::getId))
                .collect(Collectors.toList());
    }

    private void adjustSections(Section newSection) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            AdjustSectionType adjustSectionType = AdjustSectionType.valueOf(section, newSection);
            if (adjustSectionType != AdjustSectionType.NONE) {
                checkDistance(section, newSection.getDistance());
                sections.add(adjustSectionType.createSection(section, newSection));
                sections.remove(i);
                break;
            }
        }
    }

    private void checkDistance(Section oldSection, int distance) {
        if (oldSection.getDistance() <= distance) {
            throw new IllegalArgumentException("");
        }
    }

    private void checkValidateSection(Section section) {
        long count = sections.stream()
                .flatMap(sect -> sect.getStations().stream())
                .filter(station -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .count();
        if (count != 1) {
            throw new IllegalArgumentException("");
        }
    }
}
