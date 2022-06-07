package nextstep.subway.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("구간 도메인")
public class SectionTest {

	@Autowired
	private StationRepository stationRepository;

	private Station 신도림역;
	private Station 까치산역;

	@BeforeEach
	void setUp() {
		신도림역 = stationRepository.save(new Station("신도림역"));
		까치산역 = stationRepository.save(new Station("까치산역"));
	}

	@Test
	void create() {
		Section section = new Section(신도림역, 까치산역, 10);

		assertAll(() -> assertEquals(section.getSectionOrder(), 0), () -> assertEquals(section.getDistance(), 10),
				() -> assertEquals(section.getUpStation(), 신도림역), () -> assertEquals(section.getDownStation(), 까치산역));
	}

	@Test
	@DisplayName("구간 ID가 없는 경우 실패하는 테스트")
	void create_fail_1() {
		assertThrows(NullPointerException.class, () -> new Section(신도림역, new Station("잠실역"), 10));
	}

	@Test
	@DisplayName("상행역과 하행역이 같은 경우 실패하는 테스트")
	void create_fail_2() {
		assertThrows(IllegalArgumentException.class, () -> new Section(신도림역, 신도림역, 10));
	}

	@Test
	@DisplayName("구간 길이가 0이하일 때 실패하는 테스트")
	void create_fail_3() {
		assertThrows(IllegalArgumentException.class, () -> new Section(신도림역, 까치산역, 0));
	}

	@Test
	@DisplayName("상행역과 하행역 비교 테스트")
	void station_equals() {
		Station 잠실역 = stationRepository.save(new Station("잠실역"));
		Section 신도림에서까치산 = new Section(신도림역, 까치산역, 10);
		Section 신도림에서잠실 = new Section(신도림역, 잠실역, 10);
		new Section(신도림역, 잠실역, 10);

		assertAll(() -> assertTrue(신도림에서까치산.upStationEquals(신도림에서잠실.getUpStation())),
				() -> assertFalse(신도림에서까치산.downStationEquals(신도림에서잠실.getDownStation())));
	}
}
