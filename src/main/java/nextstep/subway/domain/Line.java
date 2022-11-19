package nextstep.subway.domain;


import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String color;

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
		this.name = name;
		this.color = color;
		addSection(upStation, downStation, distance);
	}

	public void addSection(Station upStation, Station downStation, Integer distance) {
		sections.addSection(this, upStation, downStation, Distance.valueOf(distance));
	}


	public void update(String name, String color) {
		this.name = name;
		this.color = color;
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

	public Sections getSections() {
		return sections.getSections();
	}

    public void removeSection(Station station) {
		sections.removeSection(station);
    }

	public List<Station> getStation() {
		return sections.getStations();
	}
}
