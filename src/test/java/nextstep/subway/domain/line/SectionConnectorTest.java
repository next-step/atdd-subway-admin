package nextstep.subway.domain.line;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.station.Station;

@DisplayName("구간 커넥터 테스트")
class SectionConnectorTest {
	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Line 이호선;
	private List<Section> 기존_구간_목록 = new ArrayList<>();
	private Sections 기존_구간들;

	@BeforeEach
	void setUp() {
		강남역 = Station.from("강남역");
		역삼역 = Station.from("역삼역");
		선릉역 = Station.from("선릉역");
		이호선 = Line.of("2호선", "green", 강남역, 역삼역, 10);
		기존_구간_목록.add(new Section(이호선, 강남역, 역삼역, 5));
		기존_구간들 = mock(Sections.class);
	}

	@Test
	@DisplayName("구간 커넥터 생성")
	void create() {
		// given
		Section 신규_구간 = new Section(이호선, 역삼역, 선릉역, 5);
		// when
		SectionConnector connector = SectionConnector.of(신규_구간, 기존_구간_목록);
		// then
		assertThat(connector).isNotNull();
	}

	@Test
	@DisplayName("상행역이 같으면 기존 구간의 상행역을 신규 구간의 하행역으로 변경")
	void addMiddleSection() {
		// given
		Section 신규_구간 = new Section(이호선, 강남역, 선릉역, 3);
		// when
		SectionConnector connector = SectionConnector.of(신규_구간, 기존_구간_목록);
		connector.connect(기존_구간들);
		// then

		assertThat(기존_구간_목록.get(0).getUpStation()).isEqualTo(선릉역);
	}

	@Test
	@DisplayName("하행역이 같으면 기존 구간의 하행역을 신규 구간의 상행역으로 변경")
	void replaceDownStationTestWhenSameDownStation() {
		// given
		Section 신규_구간 = new Section(이호선, 선릉역, 역삼역, 3);
		// when
		SectionConnector connector = SectionConnector.of(신규_구간, 기존_구간_목록);
		connector.connect(기존_구간들);
		// then
		assertThat(기존_구간_목록.get(0).getDownStation()).isEqualTo(선릉역);
	}

}