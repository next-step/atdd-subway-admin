package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
	private Sections sections;
	private Station station1;
	private Station station2;
	private Station station3;
	private Station station4;
	private Station station5;

	@BeforeEach
	public void before() {
		sections = new Sections();

		station1 = new Station("두정역");
		station2 = new Station("천안역");
		station3 = new Station("봉명역");
		station4 = new Station("쌍용역");
		station5 = new Station("아산역");

		Section section = Section.create(1L, station2, station4, 30);
		section.updateSequence(1);
		sections.init(section);
	}

	@Test
	@DisplayName("Sections 노선생성 시 init 테스트")
	public void sectionsInitTest() {
		assertThat(sections.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Sections 내 지하철역 조회 테스트")
	public void sectionsGetStationsTest() {
		//given
		//when
		List<Station> stations = sections.getAllStationsBySections();
		//then
		assertThat(stations).isEqualTo(Arrays.asList(station2, station4));
	}

	@Test
	@DisplayName("Sections 내 지하철역 없을때 에러 테스트")
	public void sectionsGetStationsErrorTest() {
		//given
		//then
		Sections emptySections = new Sections();
		//when
		assertThatThrownBy(() -> emptySections.getAllStationsBySections())
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 시작점에 구간 추가 성공 ")
	public void addSectionStartLocationSuccess() {
		//given
		Section section = Section.create(station1, station2, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station1, station2, station4);
	}

	@Test
	@DisplayName("Sections 종점에 구간 추가 성공 ")
	public void addSectionEndLocationSuccess() {
		//given
		Section section = Section.create(station4, station5, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station2, station4, station5);
	}

	@Test
	@DisplayName("Sections 중간 시작점에 구간 추가 성공 ")
	public void addSectionMiddleStartLocationSuccess() {
		//given
		Section section = Section.create(station2, station3, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station2, station3, station4);
	}

	@Test
	@DisplayName("Sections 중간 종점에 구간 추가 성공 ")
	public void addSectionMiddleEndLocationSuccess() {
		//given
		Section section = Section.create(station3, station4, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station2, station3, station4);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 길이제약조건으로 실패 ")
	public void addSectionMiddleLocationValidateLengthFail() {
		//given
		//then
		Section section = Section.create(station3, station4, 30);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 미 존재역 조건으로 실패 ")
	public void addSectionMiddleLocationNotContainFail() {
		//given
		//then
		Section section = Section.create(station1, station5, 12);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 모두 존재하는 역 조건으로 실패 ")
	public void addSectionMiddleLocationAllContainFail() {
		//given
		//then
		Section section = Section.create(station2, station4, 12);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 내 중간역 삭제 성공")
	public void deleteMiddleStationSuccess() {
		//given
		Section section = Section.create(2L,station2, station3, 12);
		sections.add(section);
		//then
		sections.deleteStation(station3);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station2, station4);
	}

	@Test
	@DisplayName("Sections 내 시작역 삭제 성공")
	public void deleteStartStationSuccess() {
		//given
		Section section = Section.create(2L,station2, station3, 12);
		sections.add(section);
		//then
		sections.deleteStation(station2);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station3, station4);
	}

	@Test
	@DisplayName("Sections 내 종점역 삭제 성공")
	public void deleteEndStationSuccess() {
		//given
		Section section = Section.create(2L,station2, station3, 12);
		sections.add(section);
		//then
		sections.deleteStation(station4);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(station2, station3);
	}
}