package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
	private List<LineStation> values = new ArrayList<>();

	protected LineStations() {

	}

	private LineStations(List<LineStation> values) {
		this.values = values;
	}

	public static LineStations of(List<LineStation> values) {
		return new LineStations(values);
	}

	public List<LineStation> getLineStationsInOrder() {
		List<LineStation> result = new ArrayList<>();

		Optional<LineStation> optionalCurrentLineStation = getFirstLineStation();
		while (optionalCurrentLineStation.isPresent()) {
			LineStation currentLineStation = optionalCurrentLineStation.get();
			result.add(currentLineStation);
			optionalCurrentLineStation = values.stream()
				.filter(lineStation -> currentLineStation.getStationId().equals(lineStation.getPreStationId()))
				.findFirst();
		}

		return result;
	}

	private Optional<LineStation> getFirstLineStation() {
		return values.stream()
			.filter(lineStation -> lineStation.getPreStationId() == null)
			.findFirst();
	}

	public int size() {
		return values.size();
	}
}
