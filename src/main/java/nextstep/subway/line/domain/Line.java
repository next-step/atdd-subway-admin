package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    @Embedded
    private Sections sections = Sections.of(new ArrayList<>());

    // 뭐때문에 이렇게 당혹해하는가????
    // TODO section 과 Stations 에서 혼동하는 나 자신....
//    @OneToMany(mappedBy = "line")
//    private List<LineStation> lineStation = new ArrayList<>();

//    public List<LineStation> getLineStation() {
//        return lineStation;
//    }

    public Line() { }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
//        addStation(section.getUpStation());
//        addStation(section.getDownStation());
        this.sections.add(section, this);

//        section.toLine(this);
//        return this.sections;
    }

//    public void addStation(Station station){
//        final LineStation e = new LineStation(this, station);
//        this.lineStation.add(e);
//        station.setLineStation(this.lineStation);
//    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        //TODO lineStation 을 쿼리로 호출
//        return lineStation.stream()
//                .filter(lineStation1 -> lineStation1.getLine().getId().equals(this.getId()))
//                .map(LineStation::getStation)
//                .collect(Collectors.toList());

//        return this.sections.getValues().stream()
//                .map(section -> Stream.of(section.getUpStation(), section.getDownStation()))
//                .flatMap(Stream::distinct)
//                .collect(Collectors.toList());
    // TODO Stream distinct 는 왜 distinct 가 안되지?
//        return this.sections.getValues().stream()
//                .map(section -> Stream.of(section.getUpStation(), section.getDownStation()))
//                .flatMap(stationStream -> stationStream)
//                .collect(Collectors.toList())
//                .stream().distinct().collect(Collectors.toList());

        return this.sections.getValues().stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList())
                .stream().distinct().collect(Collectors.toList());
    }

    public void update(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        List<Section> values = this.sections.getValues();
        values.clear();
        values.add(section);
    }
}
