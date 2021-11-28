package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionTest
{
	@Test
	@DisplayName("Section 생성 테스트")
	public void sectionCreateTest() {
		//given
		//when
		Section section = Section.create(new Station("강남역"), new Station("선릉역"));
		//then
		assertThat(section).isNotNull();
	}

	@Test
	@DisplayName("Section 생성 테스트")
	public void sectionTest() {
		//given
		//when
		Section section = Section.create(new Station("강남역"), new Station("선릉역"));
		//then
		assertThat(section).isNotNull();
	}
}