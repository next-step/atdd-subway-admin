package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;

	private String color;

	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
	private List<Section> sections = new ArrayList<>();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
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

	public void addSection(Section section) {
		this.sections.add(section);
	}
}
