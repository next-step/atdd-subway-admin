package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineColor {
	@Column(name = "color")
	private String value;

	protected LineColor() {
	}

	private LineColor(String value) {
		this.value = value;
	}

	public static LineColor from(String value) {
		return new LineColor(value);
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
		LineColor lineColor = (LineColor)o;
		return Objects.equals(value, lineColor.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
