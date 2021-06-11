package nextstep.subway.domain;

import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.exception.ValueFormatException;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections(new ArrayList<>());

    @Embedded
    private LineStations lineStations = new LineStations(new ArrayList<>());

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
        line.registerStation(upStation);
        line.registerStation(downStation);
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
        lineStations.addLineStation(lineStation);
    }

    /**
     * 비즈니스 메소드
     */
    public void change(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void registerStation(Station station) {
        if (!lineStations.contains(station)) {
            LineStation lineStation = LineStation.create(this, station);
            lineStation.setStation(station);
        }
    }

    public Sections sections() {
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
        return lineStations.upEndStation();
    }

    public void register(Station upStation, Station downStation, Section section) {
        Station existsStation = findExistsStation(upStation, downStation);
        Section existsSection = findExistsSection(existsStation, upStation);
        if (isEndStation(existsSection)) {
            connectAsEndStation(upStation, downStation, section);
            return;
        }

        existsSection.resetDistance(section.distance());

        if (isUpStation(existsStation, upStation)) {
            connectAsMiddleStationByUpStation(upStation, downStation, section, existsSection);
            return;
        }

        connectAsMiddleStationByDownStation(upStation, downStation, section);
    }

    public boolean contains(Station station) {
        return lineStations.contains(station);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
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

    public void setName(String name) {
        this.name = name;
    }

    private void connectAsMiddleStationByDownStation(Station upStation, Station downStation, Section section) {
        downStation.upSection().registerDownStation(upStation);
        connect(upStation, downStation, section);
        registerStation(upStation);
    }

    private void connectAsMiddleStationByUpStation(Station upStation, Station downStation, Section section, Section existsSection) {
        downStation.registerDownSection(existsSection);
        connect(upStation, downStation, section);
        registerStation(downStation);
    }

    private void connectAsEndStation(Station upStation, Station downStation, Section section) {
        connect(upStation, downStation, section);
        registerStation(upStation);
    }

    private boolean isEndStation(Section existsSection) {
        return existsSection == null;
    }

    private void connect(Station upStation, Station downStation, Section section) {
        upStation.registerDownSection(section);
        section.registerDownStation(downStation);
        section.registerLine(this);
    }

    private Section findExistsSection(Station existsStation, Station upStation) {
        if (isUpStation(existsStation, upStation)) {
            return existsStation.downSection();
        }
        return existsStation.upSection();
    }

    private boolean isUpStation(Station existsStation, Station upStation) {
        return existsStation.equals(upStation);
    }

    private Station findExistsStation(Station upStation, Station downStation) {
        boolean isExistsUpStation = contains(upStation);
        boolean isExistsDownStation = contains(downStation);

        validate(isExistsUpStation, isExistsDownStation);

        if (isExistsUpStation) {
            return upStation;
        }

        return downStation;
    }

    private void validate(boolean isExistsUpStation, boolean isExistsDownStation) {
        if (isExistsUpStation && isExistsDownStation) {
            throw new IllegalStateException("등록하려는 두 역이 이미 모두 노선에 포함되어 있습니다.");
        }

        if (!isExistsUpStation && !isExistsDownStation) {
            throw new IllegalStateException("등록하려는 두 역이 노선에 모두 포함되어 있지 않습니다.");
        }
    }
}
