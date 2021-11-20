package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
	private List<Section> values = new ArrayList<>();

	protected Sections() {
	}

	private Sections(List<Section> values) {
		this.values = values;
	}

	public void add(Section section) {
		this.values.add(section);
	}

	public static Sections empty() {
		return new Sections(new ArrayList<>());
	}
}
