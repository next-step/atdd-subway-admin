package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.LineTest.*;
import static nextstep.subway.line.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {
	static Section 강남_양재_구간 = new Section(1L, 신분당선, 강남, 양재, 1);
	static Section 양재_양재시민의숲_구간 = new Section(2L, 신분당선, 양재, 양재시민의숲, 2);
	static Section 양재시민의숲_청계산입구_구간 = new Section(3L, 신분당선, 양재시민의숲, 청계산입구, 3);
	static Section 청계산입구_판교_구간 = new Section(4L, 신분당선, 청계산입구, 판교, 4);

	@Test
	@DisplayName("구간의 상행역, 하행역에 따라서 이전 이후 역을 판별할 수 있다.")
	public void isPreviousAndNextTest() {
		assertAll(
			() -> assertThat(강남_양재_구간.isPreviousOf(양재_양재시민의숲_구간)).isTrue(),
			() -> assertThat(청계산입구_판교_구간.isNextOf(양재시민의숲_청계산입구_구간)).isTrue()
		);
	}

	@Test
	@DisplayName("구간의 Id 가 같으면 같은 구간이다.")
	public void equalsTest() {
		Section section1 = new Section(1L, 신분당선, 양재, 강남, 5);
		Section section2 = new Section(1L, 신분당선, 양재, 강남, 10);

		assertThat(section1).isEqualTo(section2);
	}
}