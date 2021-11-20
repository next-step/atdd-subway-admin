package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
	@Column(name = "name", unique = true)
	private String value;

	protected LineName() {
	}

	private LineName(String value) {
		this.value = value;
	}

	public static LineName from(String value) {
		return new LineName(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LineName lineName = (LineName)o;
		return Objects.equals(value, lineName.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
