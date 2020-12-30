package nextstep.subway.line.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineResponseTest {

	@DisplayName("정렬된 역 정보 확인")
	@Test
	void stationsInOrder() {
		//given
		Section section = new Section(new Station("강남역"), new Station("역삼역"), 2);
		List<Section> sections = Arrays.asList(section);

		//when
		LineResponse lineResponse = new LineResponse(sections);

		//then
		List<StationResponse> expected = Arrays
			  .asList(StationResponse.of(section.getUpStation())
					, StationResponse.of(section.getDownStation())
			  );
		assertThat(lineResponse.getStations()).containsExactly(expected.get(0), expected.get(1));
	}
}
