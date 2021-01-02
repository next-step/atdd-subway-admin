package nextstep.subway.common;

public enum StationType {
	UP_STATION("상행역"),
	DOWN_STATION("하행역"),
	NONE("-");

	StationType(String type) {
		this.type = type;
	}

	private String type;

	public String getType() {
		return type;
	}
}
