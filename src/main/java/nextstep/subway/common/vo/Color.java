package nextstep.subway.common.vo;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.logging.log4j.util.Strings;

@Embeddable
public class Color {

	private static final int MAX_LENGTH = 255;
	private static final String COLUMN_DESCRIPTION = "색상";

	@Column
	private String color;

	protected Color() {
	}

	private Color(String color) {
		validateColor(color);
		this.color = color;
	}

	public static Color generate(String color) {
		return new Color(color);
	}

	private static void validateColor(String color) {
		validateIsNotNull(color);
		validateIsNotBlank(color);
		validateMaxLength(color);
	}

	private static void validateIsNotBlank(String color) {
		if (Strings.isBlank(color)) {
			throw new IllegalArgumentException(COLUMN_DESCRIPTION + "은 빈 문자열이 아닙니다.");
		}
	}

	private static void validateMaxLength(String color) {
		if (MAX_LENGTH < color.getBytes().length) {
			throw new IllegalArgumentException(COLUMN_DESCRIPTION + "은 반드시 " + MAX_LENGTH + "byte 이하여야 합니다.");
		}
	}

	private static void validateIsNotNull(String color) {
		if (Objects.isNull(color)) {
			throw new IllegalArgumentException(COLUMN_DESCRIPTION + "은 반드시 입력되어야 합니다.");
		}
	}

	public String value() {
		return color;
	}

	public void changeColor(String color) {
		validateColor(color);
		this.color = color;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Color)) {
			return false;
		}
		Color color1 = (Color)object;
		return Objects.equals(color, color1.color);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color);
	}
}
