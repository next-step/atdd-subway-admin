package nextstep.subway.domain.line;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Embedded
    private Sections sections;

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
        this.sections = Sections.addInitialSection(Section.of(this, upStation, downStation, distance));
    }

    public void updateColor(UpdateLineRequest updateLineRequest) {
        this.name = updateLineRequest.getName();
        this.color = updateLineRequest.getColor();
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station){
        sections.remove(station);
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

    public int getTotalDistance() {
        return sections.totalDistance();
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

}
