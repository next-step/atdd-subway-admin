package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("FavoriteRepositoryTest : 정렬, 페이징 테스트")
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
}