package nextstep.subway.domain;

import nextstep.subway.exception.ValueFormatException;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    /**
     * 생성자
     */
    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    //테스트용 생성자
    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    /**
     * 생성 메소드
     */
    public static Line create(String name, String color) {
        validate(name, color);
        return new Line(name, color);
    }
    public static Line createWithSectionAndStation(String name, String color, Section section,
                                                   Station upStation, Station downStation) {
        validate(name, color);
        Line line = new Line(name, color);
        upStation.registerDownSection(section);
        section.registerDownStation(downStation);
        section.registerLine(line);
        upStation.registerLine(line);
        downStation.registerLine(line);
        return line;
    }

    private static void validate(String name, String color) {
        if (Strings.isBlank(name)) {
            throw new ValueFormatException("노선의 이름이 존재하지 않습니다.", "name", name, null);
        }

        if (Strings.isBlank(color)) {
            throw new ValueFormatException("노선의 색이 존재하지 않습니다.", "color", color, null);
        }
    }

    /**
     * 연관관계 메소드
     */
    public void addSections(Section section) {
        sections.add(section);

    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    /**
     * 비즈니스 메소드
     */
    public void change(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void registerStation(Station station) {
        LineStation lineStation = LineStation.create(this, station);
        lineStation.setStation(station);
    }

    public List<Station> stationList() {
        return lineStations.stream().map(lineStation -> lineStation.station()).collect(Collectors.toList());
    }

    public List<Section> sectionList() {
        return sections;
    }

    public List<Station> sortedStationList() {
        Station upEndStation = upEndStation();
        List<Station> resultList = new ArrayList<>();
        resultList.add(upEndStation);

        Station tempStation = upEndStation;
        while (isExistsDownSection(tempStation)) {
            Station nextStation = tempStation.downSection().downStation();
            resultList.add(nextStation);
            tempStation = nextStation;
        }
        return resultList;
    }

    private boolean isExistsDownSection(Station tempStation) {
        return tempStation.downSection() != null;
    }

    public Station upEndStation() {
        return lineStations.stream()
                .map(lineStation -> lineStation.station())
                .filter(station -> station.upSection() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행종점역이 존재하지 않습니다."));
    }

    /**
     * 그 밖의 메소드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id(), line.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public int sectionsSize() {
        return sections.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
