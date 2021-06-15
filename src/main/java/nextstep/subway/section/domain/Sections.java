package nextstep.subway.section.domain;

import nextstep.subway.station.domain.DeletePosition;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval=true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void add(Section section) {
        if (this.isEmpty()) {
            sections.add(section);
            return;
        }
        if (this.contains(section)) {
            return;
        }

        DockingPosition position = findSectionPosition(section);
        sections.add(position.positionIndex(), section);
    }

    private DockingPosition findSectionPosition(Section section) {
        DockingPosition position = DockingPosition.none();
        while (position.isNotDockedYet()) {
            position = section.dockingPositionOn(sections, position);
            position.nextIndex();
        }
        position.subIndex();

        return position;
    }

    public void delete(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("구간이 1개라서 삭제 할 수 없습니다.");
        }

        handleDeletion(findDeletePostion(station));
    }

    private DeletePosition findDeletePostion(Station station) {
        DeletePosition deletePosition = DeletePosition.None();
        while (deletePosition.isNone() && deletePosition.index() < sections.size()) {
            deletePosition = checkDeletePosition(deletePosition, station);
            deletePosition.nextIndex();
        }
        deletePosition.subtractIndex();
        deletePosition.validate();

        return deletePosition;
    }

    private DeletePosition checkDeletePosition(DeletePosition position, Station station) {
        int index = position.index();
        Section currentSection = sections.get(index);
        if (currentSection.upStationIsEqualsWith(station) && index == 0) {
            return position.typeUpInHead();
        }
        if (currentSection.upStationIsEqualsWith(station) && index == sections.size() - 1) {
            return position.typeUpInTail();
        }
        if (currentSection.upStationIsEqualsWith(station)) {
            return position.typeUpInMiddles();
        }
        if (currentSection.downStationIsEqualsWith(station) && index == sections.size() - 1) {
            return position.typeDownInTail();
        }
        return position;
    }

    private void handleDeletion(DeletePosition deletePosition) {
        int index = deletePosition.index();
        Section prevSection;
        Section nextSection;
        if (deletePosition.isUpInHead()) {
            sections.remove(index);
        }
        if (deletePosition.isUpInTail()) {
            prevSection = sections.get(index-1);
            prevSection.handleAttributesToDeleteOnTail(sections.get(index));
            sections.remove(index);
        }
        if (deletePosition.isUpInMiddles()) {
            prevSection = sections.get(index-1);
            nextSection = sections.get(index+1);
            prevSection.handleAttributesToDeleteInFrontOf(nextSection);
            sections.remove(index);
        }
        if (deletePosition.isDownInTail()) {
            sections.remove(index);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void validateConnectionWith(Section sectionIn) {
        if (sections.isEmpty()) {
            return;
        }
        alreadyInBoth(sectionIn);
        nothingInBoth(sectionIn);
    }

    private void alreadyInBoth(Section sectionIn) {
        if (sectionIn.bothStationsAreAlreadyIn(stations())) {
            throw new IllegalArgumentException("둘 다 이미 들어있는 역.");
        }
    }

    private void nothingInBoth(Section sectionIn) {
        if (sectionIn.bothStationsAreNotIn(stations())) {
            throw new IllegalArgumentException("둘 다 들어있지 않은 역.");
        }
    }

    public List<Station> stations() {
        return OrderedSections.of(this.sections).get().stream()
                .flatMap(section -> section.upDownStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> get() {
        return sections;
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

}
