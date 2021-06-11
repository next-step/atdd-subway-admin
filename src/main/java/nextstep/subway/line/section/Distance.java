package nextstep.subway.line.section;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

	@Column
	private int distance;
}
