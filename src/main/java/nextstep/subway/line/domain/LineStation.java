package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class LineStation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id")
	private Station station;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;
	private int distance;

	protected LineStation() {
	}

	private LineStation(Builder builder) {
		this.line = builder.line;
		this.station = builder.station;
		this.downStation = builder.downStation;
		this.distance = builder.distance;
	}

	public static LineStation of(Line line, Station station) {
		return new Builder()
			.line(line)
			.station(station)
			.build();
	}

	public Station getStation() {
		return station;
	}

	public Station getDownStation() {
		return downStation;
	}

	public void changeDownStation(Station downStation) {
		this.downStation = downStation;
	}

	public int getDistance() {
		return distance;
	}

	public void changeDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LineStation)) {
			return false;
		}
		LineStation that = (LineStation)o;
		return Objects.equals(line, that.line) && Objects.equals(station, that.station);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, station);
	}

	public static class Builder {
		private Line line;
		private Station station;
		private Station downStation;
		private int distance;

		public Builder() {
		}

		public Builder line(Line line) {
			this.line = line;
			return this;
		}

		public Builder station(Station station) {
			this.station = station;
			return this;
		}

		public Builder downStation(Station downStation) {
			this.downStation = downStation;
			return this;
		}

		public Builder distance(int distance) {
			this.distance = distance;
			return this;
		}

		public LineStation build() {
			return new LineStation(this);
		}
	}
}