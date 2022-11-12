package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.constant.ErrorCode;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> findStations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        validateDuplicateSection(section);
        validateNotContainAnySection(section);

        updateUpStationSection(section);
        updateDownStationSection(section);
        sections.add(section);
    }

    private void validateDuplicateSection(Section section) {
        if(isAllContainStations(section)) {
            throw new IllegalArgumentException(ErrorCode.이미_존재하는_구간.getErrorMessage());
        }
    }

    private void validateNotContainAnySection(Section section) {
        if(!isContainStation(section.getUpStation()) && !isContainStation(section.getDownStation())) {
            throw new IllegalArgumentException(ErrorCode.구간의_상행역과_하행역이_모두_노선에_포함되지_않음.getErrorMessage());
        }
    }

    private boolean isAllContainStations(Section section) {
        return findStations().containsAll(section.stations());
    }

    private boolean isContainStation(Station station) {
        return findStations().contains(station);
    }

    private void updateUpStationSection(Section newSection) {
        sections.stream().filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst().ifPresent(section -> section.updateUpStation(newSection));
    }

    private void updateDownStationSection(Section newSection) {
        sections.stream().filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst().ifPresent(section -> section.updateDownStation(newSection));
    }
}
