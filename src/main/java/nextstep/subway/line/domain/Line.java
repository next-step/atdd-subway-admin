package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.NoSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;
	private String color;

	@OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Line() {
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

	public List<Section> getSections() {
		return sections;
	}

	public Section getLongestSection() {
		return sections.stream().max(Comparator.comparingInt(Section::getDistance))
				.orElseThrow(NoSectionException::new);
	}

	public void addSection(Section... section) {
		sections.addAll(Arrays.asList(section));
	}
}
