package nextstep.subway.section.dto;

public class SectionAddCondition {
	private boolean isOldUpStation;

	private boolean isOldDownStation;

	private boolean isNewUpStation;

	private boolean isNewDownStation;

	public SectionAddCondition(boolean isOldUpStation, boolean isOldDownStation, boolean isNewUpStation, boolean isNewDownStation) {
		this.isOldUpStation = isOldUpStation;
		this.isOldDownStation = isOldDownStation;
		this.isNewUpStation = isNewUpStation;
		this.isNewDownStation = isNewDownStation;
	}

	public boolean isOldUpStation() {
		return isOldUpStation;
	}

	public boolean isBetweenAdd(){
		return this.isOldUpStation || this.isOldDownStation;
	}

	public boolean isNewUpStation() {
		return isNewUpStation;
	}

}
