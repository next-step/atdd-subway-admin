package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SectionsTest {

	private Sections sections;
	private Map<String, Section> sectionMap;
	private Map<String, Station> stationMap;

	@BeforeEach
	void setUp() {
		stationMap = new HashMap<>();
		sectionMap = new HashMap<>();

		Station 강남역 = new Station("강남역");
		Station 역삼역 = new Station("역삼역");
		Station 교대역 = new Station("교대역");
		Station 선릉역 = new Station("선릉역");
		Station 잠실역 = new Station("잠실역");
		Station 강변역 = new Station("강변역");
		ReflectionTestUtils.setField(강남역, "id", 1L);
		ReflectionTestUtils.setField(역삼역, "id", 2L);
		ReflectionTestUtils.setField(교대역, "id", 3L);
		ReflectionTestUtils.setField(선릉역, "id", 4L);
		ReflectionTestUtils.setField(잠실역, "id", 5L);
		ReflectionTestUtils.setField(강변역, "id", 6L);
		stationMap.put("강남역", 강남역);
		stationMap.put("역삼역", 역삼역);
		stationMap.put("교대역", 교대역);
		stationMap.put("선릉역", 선릉역);
		stationMap.put("잠실역", 잠실역);
		stationMap.put("강변역", 강변역);

		Section section = new Section(강남역, 역삼역, 2);
		Section upSection = new Section(교대역, 강남역, 2);
		Section downSection = new Section(역삼역, 선릉역, 2);
		Section unLinkedSection = new Section(잠실역, 강변역, 2);

		sectionMap.put("section", section);
		sectionMap.put("upSection", upSection);
		sectionMap.put("downSection", downSection);
		sectionMap.put("unLinkedSection", unLinkedSection);

		List<Section> newSections = new ArrayList<>();
		newSections.add(sectionMap.get("section"));
		sections = new Sections(newSections);
	}

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

	@DisplayName("등록된 구간 앞 또는 뒤에 구간을 추가한다.")
	@Test
	void addSection() {
		//when
		sections.add(sectionMap.get("upSection"));
		sections.add(sectionMap.get("downSection"));

		//then
		assertThat(sections.getSections()).hasSize(3);
	}

	@DisplayName("등록된 구간과 연결되지 않은 경우 구간을 추가할 수 없다.")
	@Test
	void addSectionNoLinked() {
		//when,then
		assertThatIllegalArgumentException()
			  .isThrownBy(() -> sections.add(sectionMap.get("unLinkedSection")))
			  .withMessage("잘못된 구간정보입니다.");
	}

	@DisplayName("동일한 구간을 추가할 경우 추가할 수 없다.")
	@Test
	void addSameSection() {
		//when,then
		assertThatIllegalArgumentException()
			  .isThrownBy(() -> sections.add(sectionMap.get("section")))
			  .withMessage("잘못된 구간정보입니다.");
	}

	@DisplayName("역 사이에 새로운 역을 등록한다.")
	@Test
	void addSectionInner() {
		//given
		Section section = new Section(stationMap.get("강남역"), stationMap.get("선릉역"), 5);
		Section innerSection = new Section(stationMap.get("강남역"), stationMap.get("역삼역"), 2);

		List<Section> sectionList = new ArrayList<>();
		sectionList.add(section);

		Sections sections = new Sections(sectionList);

		//when
		sections.add(innerSection);

		//then
		Section expected1 = new Section(stationMap.get("강남역"), stationMap.get("역삼역"), 2);
		Section expected2 = new Section(stationMap.get("역삼역"), stationMap.get("선릉역"), 3);

		assertThat(sections.getSections()).hasSize(2);
		assertThat(sections.getSections()).containsAll(Arrays.asList(expected1, expected2));
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 거리가 같거나 길면 추가할 수 없다")
	@Test
	void addSectionInnerWithWrongDistance() {
		//given
		Section section = new Section(stationMap.get("강남역"), stationMap.get("선릉역"), 5);
		Section innerSection = new Section(stationMap.get("강남역"), stationMap.get("역삼역"), 5);

		List<Section> sectionList = new ArrayList<>();
		sectionList.add(section);

		Sections sections = new Sections(sectionList);

		//when, then
		assertThatIllegalArgumentException()
			  .isThrownBy(() -> sections.add(innerSection))
			  .withMessage("추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
	}

	@DisplayName("다른 구간의 상행역과 다른 구간의 하행역을 새로운 구간으로 등록할 수 없다.")
	@Test
	void addSectionAlreadyRegisteredStations() {
		//given
		Section section1 = new Section(stationMap.get("강남역"), stationMap.get("역삼역"), 5);
		Section section2 = new Section(stationMap.get("역삼역"), stationMap.get("선릉역"), 5);
		Section wrongSection = new Section(stationMap.get("강남역"), stationMap.get("선릉역"), 10);

		List<Section> sectionList = new ArrayList<>();
		sectionList.add(section1);
		sectionList.add(section2);

		Sections sections = new Sections(sectionList);

		//when
		assertThatIllegalArgumentException()
			  .isThrownBy(() -> sections.add(wrongSection))
			  .withMessage("잘못된 구간정보입니다.");
	}
}
