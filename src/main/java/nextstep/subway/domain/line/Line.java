package nextstep.subway.domain.line;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.UpdateLineRequest;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_LINE_NAME", columnNames = "name"))
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @OneToOne
    @JoinColumn(
        name = "up_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_LINE_UP_STATION")
    )
    private Station upStation;

    @OneToOne
    @JoinColumn(
        name = "down_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_LINE_DOWN_STATION")
    )
    private Station downStation;

    @Embedded
    private Sections sections;

    private int distance;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(
        String name,
        String color,
        Station upStation,
        Station downStation,
        int distance
    ) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sections = Sections.addInitialSection(new Section(this, upStation, downStation, distance));
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void updateColor(UpdateLineRequest updateLineRequest) {
        this.name = updateLineRequest.getName();
        this.color = updateLineRequest.getColor();
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Station getFinalUpStation() {
        return sections.getFinalUpStation();
    }

    public Station getFinalDownStation() {
        return sections.getFinalDownStation();
    }

    public List<Station> getAllStations() {
        return sections.sortedByFinalUpStations();
    }

    public List<Section> getAllSections() {
        return sections.getSections();
    }

    public Section getFinalUpSection() {
        return sections.getFinalUpSection();
    }

    public Section getFinalDownSection() {
        return sections.getFinalDownSection();
    }
}
