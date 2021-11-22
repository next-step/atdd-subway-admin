package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {

	private static final String ERROR_MESSAGE_LONGER_THAN_EXIST_SECTION = "기존 역 사이 길이와 같거나 긴 구간은 등록이 불가합니다.";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
	private Station downStation;

	private int distance;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void assignLine(Line line) {
		this.line = line;
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public void minusDistance(int distance) {
		this.distance -= distance;
	}

	public boolean matchUpStation(Station station) {
		return upStation.equals(station);
	}

	public boolean matchDownStation(Station station) {
		return downStation.equals(station);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Line)) {
			return false;
		}

		Section section = (Section)o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", distance=" + distance +
			'}';
	}

	protected void sliceUpSection(Section addSection) {
		validateInsertableLengthBetween(addSection);
		this.upStation = addSection.getDownStation();
		minusDistance(addSection.getDistance());
	}

	protected void sliceDownSection(Section addSection) {
		validateInsertableLengthBetween(addSection);
		this.downStation = addSection.getUpStation();
		minusDistance(addSection.getDistance());
	}

	private void validateInsertableLengthBetween(Section requestSection) {
		if (distance <= requestSection.getDistance()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_LONGER_THAN_EXIST_SECTION);
		}
	}

	public void changeDownStation(Station downStation) {
		this.downStation=downStation;
	}
}