package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

	@DisplayName("정렬된 역 정보 확인")
	@Test
	void stationsInOrder() {
		//given
		Section section = new Section(new Station("강남역"), new Station("역삼역"), 2);
		Sections sections = new Sections(Arrays.asList(section));

		//when
		List<Station> stations = sections.stationsInOrder();

		//then
		assertThat(stations).containsExactly(section.getUpStation(), section.getDownStation());
	}
}
