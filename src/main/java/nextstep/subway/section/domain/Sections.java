package nextstep.subway.section.domain;

import nextstep.subway.exception.CanNotAddNewSectionException;
import nextstep.subway.exception.CanNotRemoveStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void initSections(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        checkException(section);
        addSectionMiddleBasedOnUpStation(section);
        addSectionUpBasedOnUpStation(section);
        addSectionDownBasedOnDownStation(section);
    }

    private void checkException(Section section) {
        checkDistanceBetweenSections(section);
        checkAlreadyExistSection(section);
        checkFindSameStation(section);
    }

    private void checkFindSameStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> !section.getUpStation().equals(oldSection.getUpStation())
                        && !section.getUpStation().equals(oldSection.getDownStation())
                        && !section.getDownStation().equals(oldSection.getUpStation())
                        && !section.getDownStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new CanNotAddNewSectionException("기존 구간에 일치하는 역이 없습니다.");
                });
    }

    private void checkAlreadyExistSection(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                && section.getDownStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new CanNotAddNewSectionException("이미 존재하는 역 구간입니다.");
                });
    }

    private void checkDistanceBetweenSections(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                        && !section.getDownStation().getName().equals(oldSection.getUpStation().getName())
                        && oldSection.getDistance() < section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new CanNotAddNewSectionException("추가할 구간의 거리가 기존 구간보다 더 클 수 없습니다.");
                });
    }

    private void addSectionDownBasedOnDownStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {sections.add(section);});
    }

    private void addSectionUpBasedOnUpStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getDownStation().equals(oldSection.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.remove(oldSection);
                    sections.add(section);
                    sections.add(oldSection);
                });
    }

    private void addSectionMiddleBasedOnUpStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                                && !section.getDownStation().getName().equals(oldSection.getUpStation().getName())
                                && oldSection.getDistance() > section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(section);
                    sections.add(new Section(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
    }

    public void forEach(Consumer<Section> section) {
        sections.forEach(section);
    }

    public List<StationResponse> getStationResponse() {
        return sections.stream()
                .flatMap(list -> list.getStations().stream()
                .map(StationResponse::of))
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeStation(Station removeStation) {
        //가운데역삭제
        Section foundUpSection = sections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(removeStation))
                .findFirst().orElse(null);

        Section foundDownSection = sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(removeStation))
                .findFirst().orElse(null);

        validateRemoveStation(foundUpSection, foundDownSection);
        removeMiddleStation(foundUpSection, foundDownSection);
        removeDownSectionStation(foundUpSection, foundDownSection);
        removeUpSectionStation(foundUpSection, foundDownSection);


    }

    private void validateRemoveStation(Section foundUpSection, Section foundDownSection) {
        if (sections.size() <= 1)
            throw new CanNotRemoveStationException("구간이 하나 밖에 없습니다.");

        if (foundUpSection == null && foundDownSection == null) {
            throw new CanNotRemoveStationException("삭제할 역이 없습니다.");
        }
    }

    private void removeMiddleStation(Section foundUpSection, Section foundDownSection) {
        if (foundUpSection != null && foundDownSection != null) {
            sections.remove(foundUpSection);
            sections.remove(foundDownSection);
            sections.add(new Section(foundUpSection.getUpStation(),
                    foundDownSection.getDownStation(),
                    foundUpSection.getDistance() + foundDownSection.getDistance()));
        }
    }

    private void removeDownSectionStation(Section foundUpSection, Section foundDownSection) {
        if (foundUpSection == null && foundDownSection != null) {
            sections.remove(foundDownSection);
        }
    }

    private void removeUpSectionStation(Section foundUpSection, Section foundDownSection) {
        if (foundUpSection != null && foundDownSection == null) {
            sections.remove(foundUpSection);
        }
    }
}
