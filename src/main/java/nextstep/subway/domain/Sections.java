package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static nextstep.subway.constants.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
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

        Optional<Section> upStation = sections.stream().filter(section -> section.isEqualsUpStation(newSection.getUpStation())).findFirst();
        Optional<Section> downStation = sections.stream().filter(section -> section.isEqualsDownStation(newSection.getDownStation())).findFirst();
        if (upStation.isPresent() || downStation.isPresent()) {
            validateDistance(newSection);
        }
//        if (!upStation.isPresent() && !downStation.isPresent()) {
//            상행 혹은 하행에 역 추가시 노선 길이 연장
//            updateLineDistance(newSection);
//        }

        upStation.ifPresent(section -> section.updateUpStation(newSection));
        downStation.ifPresent(section -> section.updateDownStation(newSection));

        sections.add(newSection);
    }

    private void validateNotExistsStations(Section newSection) {
        if (!getStations().contains(newSection.getUpStation()) && !getStations().contains(newSection.getDownStation())) {
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

    public void deleteSection(Station deleteStation) {
        Optional<Section> upStation = sections.stream().filter(section -> section.isEqualsUpStation(deleteStation)).findFirst();
        Optional<Section> downStation = sections.stream().filter(section -> section.isEqualsDownStation(deleteStation)).findFirst();
        if(upStation.isPresent() && downStation.isPresent()) {
            //중간 역 삭제
            //downStation 의 upStation 을 upStation 으로
            //upStation 의 downStation 을 downStation 으로
            Distance mergedDistance = downStation.get().getDistance().add(upStation.get().getDistance());
            sections.add(Section.of(upStation.get().getLine(), downStation.get().getUpStation(), upStation.get().getDownStation(), mergedDistance));
        }
        upStation.ifPresent(section -> removeSection(section));
        downStation.ifPresent(section -> removeSection(section));
    }

    private void removeSection(Section deleteSection) {
        sections.removeIf(section -> section.equals(deleteSection));
    }

    public Distance getTotalDistance() {
        return new Distance(sections.stream().mapToInt(section -> section.getDistance().getDistance()).sum());
    }
}
