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
        this(name, color);
        Section section = new Section(this, front, back, distance);
        this.sections = new LineSections(section);
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
    	this.name = name;
    	this.color = color;
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
