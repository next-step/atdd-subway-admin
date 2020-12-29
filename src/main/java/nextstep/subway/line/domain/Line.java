package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color, Station front, Station back, int distance) {
        this(name, color);
        Section nullFront = new Section(this, null, front, 0);
        Section section = new Section(this, front, back, distance);
        Section nullBack = new Section(this, back, null, 0);

        this.sections.add(nullFront);
        this.sections.add(section);
        this.sections.add(nullBack);
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new ArrayList<>();
    }

    public void update(String name, String color) {
    	this.name = name;
    	this.color = color;
    }

    public List<Station> getSortedStations() {
	    List<Station> stations = new ArrayList<>();
	    Section section = findSectionByFrontStation(sections, null);
	    while (section.getBack() != null) {
		    stations.add(section.getBack());
		    section = findSectionByFrontStation(sections, section.getBack());
	    }

	    return stations;
    }

	private static Section findSectionByFrontStation(List<Section> sections, @Nullable Station station) {
		return sections.stream()
				.filter(section -> Objects.equals(section.getFront(), station))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("cannot find station"));
	}

	public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }
}
