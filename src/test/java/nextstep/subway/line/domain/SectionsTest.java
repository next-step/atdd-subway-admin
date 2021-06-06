package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.SectionTest.*;
import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
	@Test
	@DisplayName("구간 사이의 역들을 상행 -> 하행 방향으로 정렬할 수 있다.")
	void orderStationsTest() {
		// given
		Sections 정렬되지_않은_강남에서_판교까지_구간들 = Sections.of(양재_양재시민의숲_구간, 강남_양재_구간, 청계산입구_판교_구간, 양재시민의숲_청계산입구_구간);

		// when
		List<Station> orderedStations = 정렬되지_않은_강남에서_판교까지_구간들.orderedStations();

		// then
		assertThat(orderedStations).containsExactly(강남, 양재, 양재시민의숲, 청계산입구, 판교);
	}
}
