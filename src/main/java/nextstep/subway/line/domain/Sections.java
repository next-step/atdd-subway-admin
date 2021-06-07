package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line")
	private List<Section> sections;
}
