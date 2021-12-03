package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionsTest {
	private Sections sections;
	private Station stationDuJeong;
	private Station stationCheonAn;
	private Station stationBongMyung;
	private Station stationSSangYong;
	private Station stationASan;

	@BeforeEach
	public void before() {
		sections = new Sections();

		stationDuJeong = new Station("두정역");
		stationCheonAn = new Station("천안역");
		stationBongMyung = new Station("봉명역");
		stationSSangYong = new Station("쌍용역");
		stationASan = new Station("아산역");

		Section section = Section.create(1L, stationCheonAn, stationSSangYong, 30);
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
		assertThat(stations).isEqualTo(Arrays.asList(stationCheonAn, stationSSangYong));
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
		Section section = Section.create(stationDuJeong, stationCheonAn, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationDuJeong, stationCheonAn,
			stationSSangYong);
	}

	@Test
	@DisplayName("Sections 종점에 구간 추가 성공 ")
	public void addSectionEndLocationSuccess() {
		//given
		Section section = Section.create(stationSSangYong, stationASan, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationCheonAn, stationSSangYong, stationASan);
	}

	@Test
	@DisplayName("Sections 중간 시작점에 구간 추가 성공 ")
	public void addSectionMiddleStartLocationSuccess() {
		//given
		Section section = Section.create(stationCheonAn, stationBongMyung, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationCheonAn, stationBongMyung,
			stationSSangYong);
	}

	@Test
	@DisplayName("Sections 중간 종점에 구간 추가 성공 ")
	public void addSectionMiddleEndLocationSuccess() {
		//given
		Section section = Section.create(stationBongMyung, stationSSangYong, 12);
		//then
		sections.add(section);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationCheonAn, stationBongMyung,
			stationSSangYong);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 길이제약조건으로 실패 ")
	public void addSectionMiddleLocationValidateLengthFail() {
		//given
		//then
		Section section = Section.create(stationBongMyung, stationSSangYong, 30);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 미 존재역 조건으로 실패 ")
	public void addSectionMiddleLocationNotContainFail() {
		//given
		//then
		Section section = Section.create(stationDuJeong, stationASan, 12);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 중간에 구간 추가 모두 존재하는 역 조건으로 실패 ")
	public void addSectionMiddleLocationAllContainFail() {
		//given
		//then
		Section section = Section.create(stationCheonAn, stationSSangYong, 12);
		//when
		assertThatThrownBy(() -> sections.add(section))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 내 중간역 삭제 성공")
	public void deleteMiddleStationSuccess() {
		//given
		Section section = Section.create(2L, stationCheonAn, stationBongMyung, 12);
		sections.add(section);
		//then
		sections.deleteStation(stationBongMyung);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationCheonAn, stationSSangYong);
	}

	@Test
	@DisplayName("Sections 내 시작역 삭제 성공")
	public void deleteStartStationSuccess() {
		//given
		Section section = Section.create(2L, stationCheonAn, stationBongMyung, 12);
		sections.add(section);
		//then
		sections.deleteStation(stationCheonAn);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationBongMyung, stationSSangYong);
	}

	@Test
	@DisplayName("Sections 내 종점역 삭제 성공")
	public void deleteEndStationSuccess() {
		//given
		Section section = Section.create(2L, stationCheonAn, stationBongMyung, 12);
		sections.add(section);
		//then
		sections.deleteStation(stationSSangYong);
		//when
		assertThat(sections.getAllStationsBySections()).containsExactly(stationCheonAn, stationBongMyung);
	}

	@Test
	@DisplayName("Sections 내 구간 1개만 존재 시 삭제 실패")
	public void deleteStationInSectionsSizeOneFail() {
		assertThatThrownBy(() -> sections.deleteStation(stationCheonAn))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Sections 내 미 존재 역 삭제 실패")
	public void deleteStationNotContainInSectionsFail() {
		//given
		Section section = Section.create(2L, stationCheonAn, stationBongMyung, 12);
		sections.add(section);
		//then
		assertThatThrownBy(() -> sections.deleteStation(stationDuJeong))
			.isInstanceOf(IllegalArgumentException.class);
	}
}