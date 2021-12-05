package nextstep.subway.common.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

	@Column(unique = true)
	private String name;

	protected Name() {}

	public Name(String name) {
		this.name = name;
	}

	public String printName() {
		return this.name;
	}

}
