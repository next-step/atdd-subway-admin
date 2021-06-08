package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() { }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.addSection(section);
        section.setLine(this);
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Sections getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(station -> Stream.of(station.getUpStation(), station.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> getOrderedStations() {
        return sections.orderedStations();
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

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section sectionIn) {
        // validate
        // 1. upStation과 downStation이 둘다 있으면 안됨
        // 2. upStation, downStation 둘 중에 하나라도 있어야함

        // sections 돌면서 확인한다.
        // 1) downStation이 이번 section의 앞에 있는지 확인한다.
        //  맞으면 -> 앞에 붙인다.
        // 2) upStation이 이번 section의 앞에 있는지 확인한다.
        //  맞으면 -> 안에 붙는 조건을 확인한다.
        //   조건은 -> 거리가 해당 section 거리보다 짧다.
        //    맞으면 -> 안에 붙인다.
        // 3) downStation이 이번 section의 뒤에 있는지 확인한다.
        //  맞으면 -> 안에 붙는 조건을 확인한다.
        //   조건은 -> 거리가 해당 section 거리보다 짧다.
        //    맞으면 -> 안에 붙인다.
        // 4) sections 의 마지막 section 이었다면,
        //  upStation이 이번 section의 뒤에 있는지 확인한다.
        //   맞으면 -> 뒤에 붙인다.

        if (sections.isEmpty()) {
            sections.add(sectionIn);
            return;
        }

        sections.checkIfValid(sectionIn);

        Station upStationIn = sectionIn.getUpStation();
        Station downStationIn = sectionIn.getDownStation();

        List<Section> orderedSections = sections.orderFromTopToBottom();

        for (int i = 0; i < orderedSections.size(); ++i) {
            Section section = orderedSections.get(i);

            // 1)
            if (downStationIn.equals(section.getUpStation())) {
                sections.add(i, sectionIn);
                break;
            }
            // 4)
            if (i == orderedSections.size() - 1) {
                if (upStationIn.equals(section.getDownStation())) {
                    sections.add(sectionIn);
                    break;
                }
            }

            // 2)
            if (upStationIn.equals(section.getUpStation())) {
                validateDistance(sectionIn.getDistance(), section.getDistance());

                section.setDistance(section.getDistance() - sectionIn.getDistance());
                section.setUpStation(downStationIn);
                sections.add(i, sectionIn);
                break;
            }
            // 3)
            if (downStationIn.equals(section.getDownStation())) {
                validateDistance(sectionIn.getDistance(), section.getDistance());

                section.setDistance(section.getDistance() - sectionIn.getDistance());
                section.setDownStation(upStationIn);
                sections.add(i+1, sectionIn);
                break;
            }
        }

        sectionIn.setLine(this);
    }

    private void validateDistance(int inputDistance, int existDistance) {
        if (inputDistance >= existDistance) {
            throw new IllegalArgumentException();
        }
    }
}
