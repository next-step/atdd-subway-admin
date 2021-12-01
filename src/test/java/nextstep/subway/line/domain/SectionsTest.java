package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {

	@Test
	@DisplayName("Sections 일급컬렉션 Add 테스트")
	public void SectionsAddTest() {
		//given
		Sections sections = new Sections();
		Section section = Section.create(new Station("강남역"), new Station("양재역"), 10);
		//when
		sections.init(section);
		//then
		assertThat(sections.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Sections 일급컬렉션 내 지하철역 조회 테스트")
	public void SectionsGetStationsTest() {
		//given
		Sections sections = new Sections();
		Section section = Section.create(new Station("강남역"), new Station("양재역"), 15);
		sections.init(section);
		//when
		List<Station> stations = sections.getAllStationsBySections();
		//then
		assertThat(stations.get(0).getName()).isEqualTo("강남역");
	}

	@Test
	@DisplayName("Sections 일급컬렉션 내 지하철역 없을때 에러 테스트")
	public void SectionsGetStationsErrorTest() {
		//given
		//then
		Sections sections = new Sections();
		//when
		assertThatThrownBy(() -> sections.getAllStationsBySections())
			.isInstanceOf(IllegalArgumentException.class);
	}
}