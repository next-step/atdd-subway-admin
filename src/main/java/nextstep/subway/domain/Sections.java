package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    public void addSection(Section newSection) {
        validateDuplicateSection(newSection);
        validateNotContainAnySection(newSection);

        Optional<Section> updateUpStationSection = findUpdateUpStationSection(newSection);
        Optional<Section> updateDownStationSection = findUpdateDownStationSection(newSection);
        if(!updateUpStationSection.isPresent() && !updateDownStationSection.isPresent()) {
            updateLineDistance(newSection);
        }
        updateUpStationSection.ifPresent(section -> section.updateUpStation(newSection));
        updateDownStationSection.ifPresent(section -> section.updateDownStation(newSection));
        sections.add(newSection);
    }

    private void validateDuplicateSection(Section section) {
        if(isAllContainStations(section)) {
            throw new IllegalArgumentException(ErrorCode.이미_존재하는_구간.getErrorMessage());
        }
    }

    private void validateNotContainAnySection(Section section) {
        if(isNotContainAnyStation(section)) {
            throw new IllegalArgumentException(ErrorCode.구간의_상행역과_하행역이_모두_노선에_포함되지_않음.getErrorMessage());
        }
    }

    private boolean isAllContainStations(Section section) {
        return findStations().containsAll(section.stations());
    }

    private boolean isNotContainAnyStation(Section section) {
        return findStations().stream().noneMatch(station -> section.stations().contains(station));
    }

    private void updateLineDistance(Section section) {
        section.updateLineDistance();
    }

    private Optional<Section> findUpdateUpStationSection(Section newSection) {
        return sections.stream().filter(section -> section.isSameUpStation(newSection))
                .findFirst();
    }

    private Optional<Section> findUpdateDownStationSection(Section newSection) {
        return sections.stream().filter(section -> section.isSameDownStation(newSection))
                .findFirst();
    }
}
