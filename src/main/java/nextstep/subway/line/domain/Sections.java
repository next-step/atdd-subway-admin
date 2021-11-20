package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("orders")
    private List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections.isEmpty();
    }


    public boolean isAnyMatchUpStation(Station upStation) {
        return sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
    }

    public boolean isAnyMatchDownStation(Station downStation) {
        return sections.stream().anyMatch(section -> section.getDownStation().equals(downStation));
    }

    public boolean isContainsSection(Section section) {
        return sections.contains(section);
    }

    public boolean isNewEndUpStation(Station downStation) {
        return sections.get(0).getUpStation().equals(downStation);
    }

    public boolean isNewEndDownStation(Station upStation) {
        return sections.get(sections.size() - 1).getDownStation().equals(upStation);
    }

    public Section findUpStationSame(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst().get();
    }

    public Section findDownStationSame(Station downStation) {
        return  sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst().get();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void add(int index, Section section) {
        sections.add(index, section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
    public int size() {
        return sections.size();
    }
    public void remove(int index) {
        sections.remove(index);
    }
    public void remove(Section section) {
        sections.remove(section);
    }
    public void indexOrders() {
        AtomicInteger index = new AtomicInteger();
        this.sections.forEach(section -> section.setOrders(index.getAndIncrement()));
    }


}
