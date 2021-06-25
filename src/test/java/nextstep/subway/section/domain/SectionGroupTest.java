package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DisplayName("섹션 그룹 테스트")
public class SectionGroupTest {

	private Section 홍대입구_신도림_섹션;
	private Section 을지로입구_홍대입구_섹션;
	private Section 신도림_강남_섹션;

	@BeforeEach
	void 초기화() {
		Line 이호선 = new Line("2호선", "#FFFFFF");
		Station 신도림 = new Station("신도림");
		Station 홍대입구 = new Station("홍대입구");
		Station 을지로입구 = new Station("을지로입구");
		Station 강남 = new Station("강남");
		int 홍대입구_신도림_간격 = 100;
		int 을지로입구_홍대입구_간격 = 300;
		int 신도림_강남_간격 = 200;

		홍대입구_신도림_섹션 = new Section(이호선, 홍대입구, 신도림, 홍대입구_신도림_간격);
		을지로입구_홍대입구_섹션 = new Section(이호선, 을지로입구, 홍대입구, 을지로입구_홍대입구_간격);
		신도림_강남_섹션 = new Section(이호선, 신도림, 강남, 신도림_강남_간격);
	}

	@Test
	void 생성() {
		//given

		//when
		SectionGroup 섹션_그룹 = new SectionGroup();

		//then
		assertThat(섹션_그룹).isNotNull();
	}

	@Test
	void 섹션_추가() {
		//given
		SectionGroup 섹션_그룹 = new SectionGroup();

		//when
		섹션_그룹.add(신도림_강남_섹션);
		섹션_그룹.add(홍대입구_신도림_섹션);
		섹션_그룹.add(을지로입구_홍대입구_섹션);

		//then
		assertThat(섹션_그룹.sections())
			.containsSequence(Lists.newArrayList(을지로입구_홍대입구_섹션, 홍대입구_신도림_섹션, 신도림_강남_섹션));
	}
}
