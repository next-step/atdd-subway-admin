package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;
	private String color;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(long id, String name, String color) {
		this(name, color);
		this.id = id;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this(name, color);
		this.createSection(upStation, downStation, distance);
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	private void createSection(Station upStation, Station downStation, int distance) {
		this.sections.add(new Section(this, upStation, downStation, distance));
	}

	public List<Station> getStations() {
		Set<Station> result = new LinkedHashSet<>();
		for (Section section : this.sections) {
			result.addAll(section.getStations());
		}

		return new ArrayList<>(result);
	}
}
