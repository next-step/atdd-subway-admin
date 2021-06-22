package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@DisplayName("구간 단위테스트")
public class SectionUnitTest {

	@Test
	@DisplayName("구간 내 하나의 역만 일치하는 로직 검증")
	void matchedOnlyOneStation() {
		Station 서울역 = new Station("서울역");
		Station 광명역 = new Station("광명역");
		Station 대전역 = new Station("대전역");
		Station 동대구역 = new Station("동대구역");
		Section 서울대전구간 = new Section(서울역, 대전역, 10);
		Section 서울광명구간 = new Section(서울역, 광명역, 2);
		Section 광명대전구간 = new Section(광명역, 대전역, 2);
		Section 광명동대구구간 = new Section(광명역, 동대구역, 10);

		assertThat(서울대전구간.matchedOnlyOneStationAndIncludedSection(서울광명구간)).isTrue();
		assertThat(서울대전구간.matchedOnlyOneStationAndIncludedSection(광명대전구간)).isTrue();
		assertThat(서울대전구간.matchedOnlyOneStationAndIncludedSection(광명동대구구간)).isFalse();
	}

	@Test
	@DisplayName("상행부터 하행까지 정렬기능 검증")
	void stationsFromUpToDown() {
		Station 서울역 = new Station("서울역");
		Station 광명역 = new Station("광명역");
		Station 대전역 = new Station("대전역");
		Station 동대구역 = new Station("동대구역");
		Station 부산역 = new Station("부산역");
		Section 서울광명구간 = new Section(서울역, 광명역, 2);
		Section 광명대전구간 = new Section(광명역, 대전역, 2);
		Section 대전동대구구간 = new Section(대전역, 동대구역, 2);
		Section 동대구부산구간 = new Section(동대구역, 부산역, 2);

		Sections sections = new Sections(Arrays.asList(광명대전구간, 동대구부산구간, 대전동대구구간, 서울광명구간));
		List<Station> stations = sections.stationsFromUpToDown();

		assertThat(stations).extracting("name").containsExactly("서울역", "광명역", "대전역", "동대구역", "부산역");
	}

	@Test
	@DisplayName("첫번째 역 검증")
	void findFirst() {
		Station 서울역 = new Station("서울역");
		Station 광명역 = new Station("광명역");
		Station 대전역 = new Station("대전역");
		Station 동대구역 = new Station("동대구역");
		Station 부산역 = new Station("부산역");
		Section 서울광명구간 = new Section(서울역, 광명역, 2);
		Section 광명대전구간 = new Section(광명역, 대전역, 2);
		Section 대전동대구구간 = new Section(대전역, 동대구역, 2);
		Section 동대구부산구간 = new Section(동대구역, 부산역, 2);

		Sections sections = new Sections(Arrays.asList(광명대전구간, 동대구부산구간, 대전동대구구간, 서울광명구간));
		assertThat(sections.findFirst()).isEqualTo(서울역);
	}

	@Test
	@DisplayName("마지막 역 검증")
	void findLast() {
		Station 서울역 = new Station("서울역");
		Station 광명역 = new Station("광명역");
		Station 대전역 = new Station("대전역");
		Station 동대구역 = new Station("동대구역");
		Station 부산역 = new Station("부산역");
		Section 서울광명구간 = new Section(서울역, 광명역, 2);
		Section 광명대전구간 = new Section(광명역, 대전역, 2);
		Section 대전동대구구간 = new Section(대전역, 동대구역, 2);
		Section 동대구부산구간 = new Section(동대구역, 부산역, 2);

		Sections sections = new Sections(Arrays.asList(광명대전구간, 동대구부산구간, 대전동대구구간, 서울광명구간));
		assertThat(sections.findLast()).isEqualTo(부산역);
	}
}
