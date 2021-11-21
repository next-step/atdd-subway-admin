package nextstep.subway.line;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.StationFixture;

public class LineStationFixture {
	public static LineStation 강남역() {
		return LineStation.of(
			1L,
			StationFixture.강남역().getId(),
			null,
			1);
	}

	public static LineStation 역삼역() {
		return LineStation.of(
			2L,
			StationFixture.역삼역().getId(),
			StationFixture.강남역().getId(),
			2);
	}

	public static LineStation 선릉역() {
		return LineStation.of(
			3L,
			StationFixture.선릉역().getId(),
			StationFixture.역삼역().getId(),
			3);
	}

	public static LineStation 삼성역() {
		return LineStation.of(
			4L,
			StationFixture.삼성역().getId(),
			StationFixture.선릉역().getId(),
			4);
	}
}
