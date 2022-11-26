package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.constants.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    //@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public void addSection(Section newSection) {
        validateNotExistsStations(newSection);
        validateDuplicateStations(newSection);

        Optional<Section> upStation = sections.stream().filter(section -> section.isEqualUpStation(newSection)).findFirst();
        Optional<Section> downStation = sections.stream().filter(section -> section.isEqualDownStation(newSection)).findFirst();
        if (upStation.isPresent() || downStation.isPresent()) {
            validateDistance(newSection);
        }
        if (!upStation.isPresent() || !downStation.isPresent()) {
            //상행 혹은 하행에 역 추가(노선 연장)
            updateLineDistance(newSection);
        }

        upStation.ifPresent(section -> section.updateUpStation(newSection));
        downStation.ifPresent(section -> section.updateDownStation(newSection));

        sections.add(newSection);
    }

    private void updateLineDistance(Section newSection) {
        newSection.updateLineDistance(newSection);
    }

    private void validateNotExistsStations(Section newSection) {
        if (sections.stream().noneMatch(section -> section.getStations().contains(newSection.getStations()))) {
            throw new IllegalArgumentException(NOT_EXISTS_SECTION_STATION_MSG);
        }
    }

    private void validateDuplicateStations(Section newSection) {
        if (sections.stream().anyMatch(section -> section.getStations().containsAll(newSection.getStations()))) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_STATION_MSG);
        }
    }

    private void validateDistance(Section newSection) {
        if (sections.stream().anyMatch(section -> section.getDistance().isSameOrLonger(newSection.getDistance()))) {
            throw new IllegalArgumentException(INVALID_DISTANCE_MSG);
        }
    }

    public List<Station> getStations() {
        return sections.stream().map(section -> section.getStations()).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }
}
