package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    private static final int MINIMUM_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sectionGroup = new ArrayList<>();

    protected Sections() {
    }

    public static Sections empty() {
        return new Sections();
    }

    public List<Section> getSectionGroup() {
        return Collections.unmodifiableList(sectionGroup);
    }

    public void add(Section newSection) {
        if (sectionGroup.isEmpty()) {
            sectionGroup.add(newSection);
            return;
        }
        validAlreadyAdded(newSection);
        validNotAdded(newSection);
        updateUpStation(newSection);
        sectionGroup.add(newSection);
    }

    private void validNotAdded(Section newSection) {
        boolean duplicateStation = sectionGroup.stream()
                .anyMatch(item -> item.containsStation(newSection));

        if (!duplicateStation) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
        }
    }

    private void validAlreadyAdded(Section newSection) {
        boolean duplicateUpStation = sectionGroup.stream()
                .anyMatch(item -> item.equalsUpStation(newSection.getUpStation()));

        boolean duplicateDownStation = sectionGroup.stream()
                .anyMatch(item -> item.equalsDownStation(newSection.getDownStation()));

        if (duplicateUpStation && duplicateDownStation) {
            throw new IllegalArgumentException("이미 구역의 역들이 등록 되어 있습니다.");
        }

    }

    private void updateUpStation(Section newSection) {
        sectionGroup.stream()
                .filter(section -> section.equalsUpStation(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpSection(newSection));
    }

    public int size() {
        return sectionGroup.size();
    }

    public void removeSection(Station targetStation) {
        validSize();
        Optional<Section> upSection = sectionGroup.stream()
                .filter(section -> section.equalsUpStation(targetStation))
                .findFirst();
        Optional<Section> downSection = sectionGroup.stream()
                .filter(section -> section.equalsDownStation(targetStation))
                .findFirst();

        if (upSection.isPresent() && downSection.isPresent()) {
            mergeSection(upSection, downSection);
            return;
        }
        removeSection(upSection, downSection);
    }

    private void validSize() {
        if (size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("하나 이상 구간일때만 제거 할 수 있습니다.");
        }
    }

    private void removeSection(Optional<Section> upSection, Optional<Section> downSection) {
        if (upSection.isPresent()) {
            sectionGroup.remove(upSection.get());
            return;
        }
        if (downSection.isPresent()) {
            sectionGroup.remove(downSection.get());
            return;
        }
    }

    private void mergeSection(Optional<Section> upSection, Optional<Section> downSection) {
        Section section = upSection.get();
        Section mergeSection = downSection.get();
        section.merge(mergeSection);
        sectionGroup.remove(mergeSection);
    }

    public List<Station> getStations() {
        return sectionGroup.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
