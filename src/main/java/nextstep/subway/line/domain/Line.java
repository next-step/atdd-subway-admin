package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("orders")
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {

        if (sections.isEmpty()) {
            addSectionToList(upStation, downStation, distance);
            return;
        }
        boolean isUpStationSame = sections.stream().anyMatch(section -> section.getUpStation().equals(upStation));
        boolean isDownStationSame = sections.stream().anyMatch(section -> section.getDownStation().equals(downStation));

        if (isDownStationSame && isUpStationSame) {
            throw new IllegalArgumentException("이미 등록된 Section 입니다.");
        }
        if(!getStations().contains(upStation) && !getStations().contains(downStation) ){
            throw new IllegalArgumentException("상행, 하행 모두 등록되지 않은 역입니다.");
        }
        addNewEndUpStation(upStation, downStation, distance);
        addNewEndDownStation(upStation, downStation, distance);

        if (isUpStationSame) {
            upStationSameAndDownStationDiff(upStation, downStation, distance);
        }
        if (isDownStationSame) {
            downStationSameAndUpStationDiff(upStation, downStation, distance);
        }
    }

    private void addNewEndUpStation(Station upStation, Station downStation, int distance) {
        System.out.println("upStation = " + upStation);
        System.out.println("downStation = " + downStation);
        boolean isNewEndUpStation = sections.get(0).getUpStation().equals(downStation);
        if (isNewEndUpStation) {
            addSectionToList(0, upStation, downStation, distance);
        }
    }

    private void addNewEndDownStation(Station upStation, Station downStation, int distance) {
        boolean isNewEndDownStation = sections.get(sections.size() - 1).getDownStation().equals(upStation);
        if (isNewEndDownStation) {
            addSectionToList(upStation, downStation, distance);
        }
    }

    private void addSectionToList(int index, Station upStation, Station downStation, int distance) {
        this.sections.add(index, new Section(this, upStation, downStation, distance));
        indexOrders();
    }

    private void addSectionToList( Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
        indexOrders();
    }

    private void upStationSameAndDownStationDiff(Station upStation, Station downStation, int distance) {
        Section findSection = sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst().get();

        if (findSection.getDistance() <= distance) {
            throw new IllegalArgumentException("등록하려는 구간 길이가 더 큽니다.");
        }
        addSectionToList(sections.indexOf(findSection) + 1, downStation, findSection.getDownStation(), distance);
        findSection.updateDownStation(downStation, findSection.getDistance() - distance);
    }

    private void downStationSameAndUpStationDiff(Station upStation, Station downStation, int distance) {
        Section findSection = sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst().get();

        if (findSection.getDistance() <= distance) {
            throw new IllegalArgumentException("등록하려는 구간 길이가 더 큽니다.");
        }
        addSectionToList(sections.indexOf(findSection), findSection.getUpStation(), upStation, distance);
        findSection.updateUpStation(upStation, findSection.getDistance() - distance);
    }

    private void indexOrders() {
        AtomicInteger index = new AtomicInteger();
        this.sections.forEach(section -> section.setOrders(index.getAndIncrement()));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {

        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
