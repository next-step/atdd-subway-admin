package nextstep.subway.domain;

import nextstep.subway.consts.ErrorMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTION_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections = new ArrayList<>(Arrays.asList(section));
    }

    public void add(Section section) {
        checkStation(section);
        sections.add(section);
    }

    private void checkStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isCheckStation(newSection))
                .findFirst()
                .ifPresent(section -> section.changeStation(newSection));
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeStation(Station station) {
        Optional<Section> upSection = findSectionbyUpStation(station);
        Optional<Section> downSection = findSectionbyDownSataion(station);

        checkSectionSize();
        checkSectionInLine(upSection, downSection);

        if (upSection.isPresent() && downSection.isPresent()) {
            deleteMiddle(upSection.get(), downSection.get());
            return;
        }
        checkDeleteEnd(upSection, downSection);
    }

    private void checkSectionInLine(Optional<Section> upSection, Optional<Section> downSection) {
        if (!upSection.isPresent() && !downSection.isPresent()) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_NO_STATION_LINE);
        }
    }

    private void checkSectionSize() {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_NOT_REMOVE_SECTION);
        }
    }

    private void checkDeleteEnd(Optional<Section> upSection, Optional<Section> downSection) {
        upSection.ifPresent(this::removeEndSection);
        downSection.ifPresent(this::removeEndSection);
    }

    private void removeEndSection(Section section) {
        sections.remove(section);
    }

    private void deleteMiddle(Section upSection, Section downSection) {
        downSection.refreshWith(upSection);
        sections.remove(upSection);
    }

    private Optional<Section> findSectionbyUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSectionbyDownSataion(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst();
    }
}
