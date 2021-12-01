package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

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
        validAlreadyAdded(newSection);
        updateUpStation(newSection);
        sectionGroup.add(newSection);
    }

    private void validAlreadyAdded(Section newSection) {
        boolean duplicateUpstation = sectionGroup.stream()
                .anyMatch(item -> item.equalsUpStation(newSection));

        boolean duplicateDownstation = sectionGroup.stream()
                .anyMatch(item -> item.equalsDownStation(newSection));

        if (duplicateUpstation && duplicateDownstation) {
            throw new IllegalArgumentException("이미 구역의 역들이 등록 되어 있습니다.");
        }

    }

    private void updateUpStation(Section newSection) {
        sectionGroup.stream()
                .filter(section -> section.equalsUpStation(newSection))
                .findFirst()
                .ifPresent(section -> section.updateUpSection(newSection));
    }

    public int size() {
        return sectionGroup.size();
    }

    public List<Station> getStations() {
        return sectionGroup.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

}
