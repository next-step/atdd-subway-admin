package nextstep.subway.domain;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private int distance;

    protected Line() {

    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public int compareToDistance(int distance) {
        return Integer.compare(this.distance, distance);
    }

    public boolean isContainStation(Station station) {
        return sections.isContainStation(station);
    }

    public Sections getSections() {
        return sections;
    }

    public List<Section> addAndGetSections(Section newSection, Station requestUpStation, Station requestDownStation) {
        List<Section> sections = this.sections.addAndGetSections(newSection,
                checkAndGetNewStation(requestUpStation, requestDownStation), requestUpStation, requestDownStation);
        sections.forEach(section -> section.setLine(this));
        return sections;
    }

    private Station checkAndGetNewStation(Station upStation, Station downStation) {
        boolean isContainUpStation = this.isContainStation(upStation);
        boolean isContainDownStation = this.isContainStation(downStation);
        if(isContainUpStation && isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_EXIST_SECTION.getMessage());
        }
        if(!isContainUpStation && !isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.NO_EXIST_STATIONS.getMessage());
        }
        return isContainUpStation ? downStation : upStation;
    }

    public void initSection(Section section) {
        sections.init(section).stream().forEach(newSection -> newSection.setLine(this));
    }
}
