package nextstep.subway.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineTest {

	@Autowired
	StationRepository stationRepository;

	@Autowired
	SectionRepository sectionRepository;

	private Station 신도림역;
	private Station 까치산역;
	private Station 잠실역;

	@BeforeEach
	void setUp() {
		신도림역 = stationRepository.save(new Station("신도림역"));
		까치산역 = stationRepository.save(new Station("까치산역"));
		잠실역 = stationRepository.save(new Station("잠실역"));
	}

	@Test
	void create() {
		Section 신도림에서까치산 = sectionRepository.save(new Section(신도림역, 까치산역, 10));

		Line line = new Line("2호선", "bg-red-600", 신도림에서까치산);
		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 1),
				() -> assertEquals(line.getSections().getSections().get(0).getUpStation(), 신도림역),
				() -> assertEquals(line.getSections().getSections().get(0).getDownStation(), 까치산역),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 10));
	}

	@Test
	void 구간_추가_첫번째로() {
		Section 신도림에서잠실 = sectionRepository.save(new Section(신도림역, 잠실역, 4));
		Section 까치산에서신도림 = sectionRepository.save(new Section(까치산역, 신도림역, 10));

		Line line = new Line("2호선", "bg-red-600", 신도림에서잠실);
		line.add(까치산에서신도림);
		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 2),
				() -> assertEquals(line.getSections().getSections().get(0), 까치산에서신도림),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 10),
				() -> assertEquals(line.getSections().getSections().get(1), 신도림에서잠실),
				() -> assertEquals(line.getSections().getSections().get(1).getDistance(), 4));
	}

	@Test
	void 구간_추가_마지막으로() {
		Section 까치산에서신도림 = sectionRepository.save(new Section(까치산역, 신도림역, 10));
		Section 신도림에서잠실 = sectionRepository.save(new Section(신도림역, 잠실역, 4));

		Line line = new Line("2호선", "bg-red-600", 까치산에서신도림);
		line.add(신도림에서잠실);

		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 2),
				() -> assertEquals(line.getSections().getSections().get(0), 까치산에서신도림),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 10),
				() -> assertEquals(line.getSections().getSections().get(1), 신도림에서잠실),
				() -> assertEquals(line.getSections().getSections().get(1).getDistance(), 4));
	}

	@Test
	void 구간_추가_중간앞에() {
		Station 강남역 = stationRepository.save(new Station("강남역"));

		Section 신도림에서잠실 = sectionRepository.save(new Section(신도림역, 잠실역, 10));
		Section 신도림에서강남 = sectionRepository.save(new Section(신도림역, 강남역, 4));

		Line line = new Line("2호선", "bg-red-600", 신도림에서잠실);
		line.add(신도림에서강남);

		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 2),
				() -> assertEquals(line.getSections().getSections().get(0), 신도림에서강남),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 4),
				() -> assertEquals(line.getSections().getSections().get(1), 신도림에서잠실),
				() -> assertEquals(line.getSections().getSections().get(1).getDistance(), 6));
	}

	@Test
	void 구간_추가_마지막_하나전에() {
		Station 강남역 = stationRepository.save(new Station("강남역"));

		Section 신도림에서잠실 = sectionRepository.save(new Section(신도림역, 잠실역, 10));
		Section 강남역에서잠실 = sectionRepository.save(new Section(강남역, 잠실역, 4));

		Line line = new Line("2호선", "bg-red-600", 신도림에서잠실);
		line.add(강남역에서잠실);

		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 2),
				() -> assertEquals(line.getSections().getSections().get(0), 신도림에서잠실),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 6),
				() -> assertEquals(line.getSections().getSections().get(1), 강남역에서잠실),
				() -> assertEquals(line.getSections().getSections().get(1).getDistance(), 4));
	}

	@Test
	void 상행역_하행역_라인에존재는경우_추가_실패_테스트() {
		Station 강남역 = stationRepository.save(new Station("강남역"));

		Section 까치산역에서신도림 = sectionRepository.save(new Section(까치산역, 신도림역, 10));
		Section 신도림역에서강남역 = sectionRepository.save(new Section(신도림역, 강남역, 4));
		Section 강남역에서잠실역 = sectionRepository.save(new Section(강남역, 잠실역, 4));
		Section 신도림역에서잠실역 = sectionRepository.save(new Section(신도림역, 잠실역, 1));
		Section 까치산역에서강남역 = sectionRepository.save(new Section(까치산역, 강남역, 1));

		Line line = new Line("2호선", "bg-red-600", 까치산역에서신도림);
		line.add(신도림역에서강남역);
		line.add(강남역에서잠실역);

		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 3),
				() -> assertEquals(line.getSections().getSections().get(0), 까치산역에서신도림),
				() -> assertEquals(line.getSections().getSections().get(1), 신도림역에서강남역),
				() -> assertEquals(line.getSections().getSections().get(2), 강남역에서잠실역),
				() -> assertEquals(line.getSections().getSections().get(2).getDistance(), 4),
				() -> assertThrows(RuntimeException.class, () -> line.add(신도림역에서잠실역)),
				() -> assertThrows(RuntimeException.class, () -> line.add(까치산역에서강남역)));
	}

	@Test
	void 구간_추가_여러개() {
		Station 대림역 = stationRepository.save(new Station("대림역"));
		Station 신림역 = stationRepository.save(new Station("신림역"));
		Station 강남역 = stationRepository.save(new Station("강남역"));
		Station 역삼역 = stationRepository.save(new Station("역삼역"));

		// 까치산-신도림-대림-신림-강남-역삼-잠실
		Section 신도림역에서강남역 = sectionRepository.save(new Section(신도림역, 강남역, 20));
		Section 강남역에서잠실역 = sectionRepository.save(new Section(강남역, 잠실역, 20)); // 맨뒤추가
		Section 역삼역에서잠실역 = sectionRepository.save(new Section(역삼역, 잠실역, 6)); // 경로차이발생
		Section 신림역에서강남역 = sectionRepository.save(new Section(신림역, 강남역, 4)); // 경로차이발생
		Section 신도림역에서대림역 = sectionRepository.save(new Section(신도림역, 대림역, 5)); // 경로차이발생
		Section 까치산역에서신도림역 = sectionRepository.save(new Section(까치산역, 신도림역, 7)); // 맨앞추가

		Line line = new Line("2호선", "bg-red-600", 신도림역에서강남역);
		line.add(강남역에서잠실역);
		line.add(역삼역에서잠실역);
		line.add(신림역에서강남역);
		line.add(신도림역에서대림역);
		line.add(까치산역에서신도림역);

		assertAll(() -> assertEquals(line.getName(), "2호선"),
				() -> assertEquals(line.getSections().getSections().size(), 6),
				() -> assertEquals(line.getSections().getSections().get(0), 까치산역에서신도림역),
				() -> assertEquals(line.getSections().getSections().get(0).getDistance(), 7),
				() -> assertEquals(line.getSections().getSections().get(1), 신도림역에서대림역),
				() -> assertEquals(line.getSections().getSections().get(1).getDistance(), 5),
				() -> assertEquals(line.getSections().getSections().get(2), 신도림역에서강남역),
				() -> assertEquals(line.getSections().getSections().get(2).getDistance(), 11),
				() -> assertEquals(line.getSections().getSections().get(3), 신림역에서강남역),
				() -> assertEquals(line.getSections().getSections().get(3).getDistance(), 4),
				() -> assertEquals(line.getSections().getSections().get(4), 강남역에서잠실역),
				() -> assertEquals(line.getSections().getSections().get(4).getDistance(), 14),
				() -> assertEquals(line.getSections().getSections().get(5), 역삼역에서잠실역),
				() -> assertEquals(line.getSections().getSections().get(5).getDistance(), 6));
	}
}
