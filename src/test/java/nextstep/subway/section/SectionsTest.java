package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.common.exception.BadParameterException;
import nextstep.subway.common.exception.ResourceNotFoundException;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class SectionsTest {
	@Autowired
	private StationRepository stationRepository;

	@Test
	void getStations() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");
		Station station4 = new Station("강남역");

		Section section1 = new Section(station3, station2, 5);
		Section section2 = new Section(station1, station3, 7);
		Section section3 = new Section(station2, station4, 10);

		Sections sections = new Sections(Arrays.asList(section3, section1, section2));

		assertThat(sections.getStationsList()).isEqualTo(Arrays.asList(station1, station3, station2, station4));
	}

	@Test
	@DisplayName("빈 상태에서 add시 해당 section 추가")
	void add_success() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");

		Section section = new Section(station1, station2, 5);
		Sections sections = new Sections();
		sections.add(section);

		assertThat(sections.contains(section)).isTrue();
	}

	@Test
	@DisplayName("동일한 구간 삽입 시 예외")
	void add_ExactlyEqualSection_Exception() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");

		Section section = new Section(station1, station2, 5);
		Sections sections = new Sections();
		sections.add(section);

		assertThatThrownBy(() -> {
			sections.add(section);
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("상행역과 하행역이 모두 노선 구간으로 등록되어 있습니다.");
	}

	@Test
	@DisplayName("상행역, 하행역 중 어느것도 노선 내 구간에 포함되지 않은 구간을 삽입 시 예외")
	void add_hasNoConnectedStationSection_Exception() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");
		Station station4 = new Station("강남역");

		Section section = new Section(station1, station2, 5);
		Section section2 = new Section(station3, station4, 5);
		Sections sections = new Sections();
		sections.add(section);

		assertThatThrownBy(() -> {
			sections.add(section2);
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("등록하려는 구간의 상행역과 하행역이 현재 노선 구간에 포함되어 있지 않습니다.");
	}

	@Test
	@DisplayName("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 긴 경우 예외")
	void add_insideSectionWithTooFarDistance_Exception() {
		Station station1 = new Station("삼성역");
		Station station2 = new Station("선릉역");
		Station station3 = new Station("역삼역");

		Section section = new Section(station1, station2, 5);
		Section section2 = new Section(station1, station3, 5);
		Section section3 = new Section(station1, station3, 10);
		Sections sections = new Sections();
		sections.add(section);

		assertThatThrownBy(() -> {
			sections.add(section2);
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");

		assertThatThrownBy(() -> {
			sections.add(section3);
		}).isInstanceOf(BadParameterException.class)
			.hasMessage("추가하려는 구간의 길이가 기존에 존재하는 길이와 같거나 깁니다.");
	}

	@Test
	@DisplayName("3개의 역에서 상행 종점을 삭제")
	void deleteSectionByStationId_success1() {
		Station 삼성역 = stationRepository.save(new Station(StationAcceptanceTest.삼성역.getName()));
		Station 선릉역 = stationRepository.save(new Station(StationAcceptanceTest.선릉역.getName()));
		Station 역삼역 = stationRepository.save(new Station(StationAcceptanceTest.역삼역.getName()));
		Section section1 = new Section(삼성역, 선릉역, 5);
		Section section2 = new Section(선릉역, 역삼역, 5);
		Sections expected = new Sections();
		expected.add(section2);
		Sections actual = new Sections();
		actual.add(section1);
		actual.add(section2);

		actual.deleteSectionByStationId(삼성역.getId());

		assertThat(expected).isEqualTo(actual);
	}

	@Test
	@DisplayName("3개의 역에서 중간 역을 삭제")
	void deleteSectionByStationId_success2() {
		Station 삼성역 = stationRepository.save(new Station(StationAcceptanceTest.삼성역.getName()));
		Station 선릉역 = stationRepository.save(new Station(StationAcceptanceTest.선릉역.getName()));
		Station 역삼역 = stationRepository.save(new Station(StationAcceptanceTest.역삼역.getName()));

		Section section1 = new Section(삼성역, 선릉역, 5);
		Section section2 = new Section(선릉역, 역삼역, 5);
		Section section3 = new Section(삼성역, 역삼역, 10);
		Sections expected = new Sections();
		expected.add(section3);

		Sections actual = new Sections();
		actual.add(section1);
		actual.add(section2);

		actual.deleteSectionByStationId(선릉역.getId());

		assertThat(expected).isEqualTo(actual);
	}

	@Test
	@DisplayName("3개의 역에서 하행 종점을 삭제")
	void deleteSectionByStationId_success3() {
		Station 삼성역 = stationRepository.save(new Station(StationAcceptanceTest.삼성역.getName()));
		Station 선릉역 = stationRepository.save(new Station(StationAcceptanceTest.선릉역.getName()));
		Station 역삼역 = stationRepository.save(new Station(StationAcceptanceTest.역삼역.getName()));
		Section section1 = new Section(삼성역, 선릉역, 5);
		Section section2 = new Section(선릉역, 역삼역, 5);
		Sections expected = new Sections();
		expected.add(section1);
		Sections actual = new Sections();
		actual.add(section1);
		actual.add(section2);

		actual.deleteSectionByStationId(역삼역.getId());

		assertThat(expected).isEqualTo(actual);
	}

	@Test
	@DisplayName("구간이 하나인 노선에서 구간을 제거할 경우 예외")
	void deleteSectionByStationId_hasOnlyOneSectionLine_exception() {
		Station 삼성역 = stationRepository.save(new Station(StationAcceptanceTest.삼성역.getName()));
		Station 선릉역 = stationRepository.save(new Station(StationAcceptanceTest.선릉역.getName()));
		Section section1 = new Section(삼성역, 선릉역, 5);
		Sections sections = new Sections();
		sections.add(section1);

		assertThatThrownBy(() -> sections.deleteSectionByStationId(삼성역.getId()))
			.isInstanceOf(BadParameterException.class)
			.hasMessage("구간이 하나만 존재하는 노선입니다.");
	}

	@Test
	@DisplayName("노선에 존재하지 않는 구간을 삭제하려 하는 경우 예외")
	void deleteSectionByStationId_hasNotSection_exception() {
		Station 삼성역 = stationRepository.save(new Station(StationAcceptanceTest.삼성역.getName()));
		Station 선릉역 = stationRepository.save(new Station(StationAcceptanceTest.선릉역.getName()));
		Station 역삼역 = stationRepository.save(new Station(StationAcceptanceTest.역삼역.getName()));
		Station 강남역 = stationRepository.save(new Station(StationAcceptanceTest.강남역.getName()));
		Section section1 = new Section(삼성역, 선릉역, 5);
		Section section2 = new Section(선릉역, 역삼역, 5);
		Sections sections = new Sections();
		sections.add(section1);
		sections.add(section2);

		assertThatThrownBy(() -> sections.deleteSectionByStationId(강남역.getId()))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("존재하지 않는 구간입니다.");
	}
}
