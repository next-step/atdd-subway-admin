package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    @SortNatural
    private SortedSet<Section> sections = new TreeSet<>();

    public Sections() {
    }

    public TreeSet<Section> getSections() {
        return (TreeSet<Section>) sections;
    }

    public void add(Section addedSection) {

        //validateTwoStationAlreadyExist(addedSection);
        sections.add(addedSection);

        sections.stream().forEach(section -> {
            System.out.println(section.getUpStation().getName() + ", " + section.getDownStation().getName());
        });

        System.out.println("====================");

    }

    private void validateTwoStationAlreadyExist(Section addedSection) {
        boolean isDownStationExist = stations().contains(addedSection.getDownStation());
        boolean isUpStationExist = stations().contains(addedSection.getUpStation());

        if (isDownStationExist && isUpStationExist) {
            throw new IllegalArgumentException();
        }
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        stations.add(sections.first().getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }
}
