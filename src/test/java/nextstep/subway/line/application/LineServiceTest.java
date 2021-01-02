package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("FavoriteRepositoryTest : 정렬, 페이징 테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class LineServiceTest {

	@Autowired
	LineService lineService;

	private List<LineResponse> savedlineResponses;

	@BeforeEach
	void saveInitData() {
		savedlineResponses = Arrays.asList(
			lineService.saveLine(new LineRequest("2호선", "green")),
			lineService.saveLine(new LineRequest("5호선", "purple"))
		);
		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@Test
	void findAllLines() {
		List<LineResponse> lineResponses = lineService.findAllLines();
		assertThat(lineResponses)
			.hasSize(savedlineResponses.size())
			.usingElementComparator(Comparator.comparing(LineResponse::getId))
			.containsAll(lineResponses);
	}

	@Test
	void findOne() {
		LineResponse 저장해둔_이호선_응답 = savedlineResponses.get(0);
		LineResponse lineResponse = lineService.findOne(저장해둔_이호선_응답.getId());
		assertAll(
			() -> assertThat(lineResponse.getId()).isEqualTo(저장해둔_이호선_응답.getId()),
			() -> assertThat(lineResponse.getName()).isEqualTo(저장해둔_이호선_응답.getName()),
			() -> assertThat(lineResponse.getColor()).isEqualTo(저장해둔_이호선_응답.getColor())
		);
	}

	@Test
	void update() {
		LineResponse 저장해둔_이호선_응답 = savedlineResponses.get(0);

		lineService.update(저장해둔_이호선_응답.getId(), new LineRequest("3호선", "orange"));

		assertThat(lineService.findOne(저장해둔_이호선_응답.getId()))
			.hasFieldOrPropertyWithValue("name", "3호선")
			.hasFieldOrPropertyWithValue("color", "orange");
	}

	@Test
	void delete() {
		LineResponse 저장해둔_이호선_응답 = savedlineResponses.get(0);

		lineService.deleteLineById(저장해둔_이호선_응답.getId());

		assertThatThrownBy(() -> lineService.findOne(저장해둔_이호선_응답.getId()))
			.isInstanceOf(NoSuchElementException.class);
	}
}