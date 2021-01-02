package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private LineSections sections;

    public Line() {
    }

    public Line(String name, String color, Station front, Station back, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new LineSections(new Section(this, front, back, distance));
    }

    public void update(String name, String color) {
    	this.name = name;
    	this.color = color;
    }

    public void addSection(Station front, Station back, int distance) {
        this.sections.addSection(new Section(this, front, back, distance));
    }

    public List<Station> getSortedStations() {
	    return this.sections.getSortedStations();
    }

	public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
