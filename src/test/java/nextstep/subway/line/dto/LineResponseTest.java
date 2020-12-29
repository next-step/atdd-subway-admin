package nextstep.subway.line.dto;

import nextstep.subway.section.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineResponseTest {

	class TestSection extends Section {
		public TestSection(Long id) {

		}
	}

	@DisplayName("노선정보 응답객체 생성시 구간정보를 역 목록으로 변환한다.")
	@Test
	void stations() {
		//givne
//		Arrays.asList(new Station(1L, "강남역"));
//		List<Section> sections = Arrays.asList(new Section(), new Section());

		//when
//		LineResponse lineResponse = new LineResponse(null, null, null, sections, null, null);

		//then
//		List<Station> expected = Arrays.asList();
//		for (int i = 0; i < expected.size(); i++) {
//			assertThat(expected.get(i)).isEqualTo(lineResponse.getStations().get(0));
//		}
	}
}
