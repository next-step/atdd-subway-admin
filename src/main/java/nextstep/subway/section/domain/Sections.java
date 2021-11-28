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

    public void add(Section section) {
        sectionGroup.add(section);
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
