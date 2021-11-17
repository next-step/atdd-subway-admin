package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "line")
	private List<Section> values = new ArrayList<>();

	protected Sections() {

	}

	public void add(Section value) {
		values.add(value);
	}
}
