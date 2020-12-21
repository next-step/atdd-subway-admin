package nextstep.subway.line.application;

import static nextstep.subway.line.LineTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import nextstep.subway.BaseTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("LineService 단위테스트")
class LineServiceTest extends BaseTest {

	@Autowired
	private LineService lineService;

	@BeforeEach
	void setup() {
		lineService.saveLine(LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE1_COLOR));
		lineService.saveLine(LineRequest.of(EXAMPLE_LINE2_NAME, EXAMPLE_LINE2_COLOR));
	}

	@DisplayName("saveLine 메서드는 Line을 생성할 수 있다.")
	@Test
	void saveLine() {
		String name = "3호선";
		String color = "주황색";

		LineResponse lineResponse = lineService.saveLine(LineRequest.of(name, color));

		assertAll(
			() -> assertThat(lineResponse.getId()).isNotNull(),
			() -> assertThat(lineResponse.getName()).isEqualTo(name),
			() -> assertThat(lineResponse.getColor()).isEqualTo(color)
		);
	}

	@DisplayName("saveLine 메서드는 동일한 노선이름을 전달하면 DataIntegrityViolationException이 발생한다.")
	@Test
	void saveLineDuplicateName() {
		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> {
				lineService.saveLine(LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE2_COLOR));
			});
	}
}