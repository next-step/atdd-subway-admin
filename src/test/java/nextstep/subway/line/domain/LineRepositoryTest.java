package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {
	@Autowired
	private LineRepository lineRepository;

	@DisplayName("DB: Line 저장 테스트")
	@Test
	void saveTest() {
		// given
		Line given = new Line("2호선", "green");

		// when
		Line line = lineRepository.save(given);

		// then
		assertAll(
			() -> assertThat(line).isNotNull(),
			() -> assertThat(line.getId()).isNotNull()
		);
	}

	@DisplayName("DB: Line 중복된 노선이름 저장 테스트")
	@Test
	void saveDuplicateNameTest() {
		// given
		lineRepository.save(new Line("2호선", "green"));

		// when
		assertThatThrownBy(() -> lineRepository.save(new Line("2호선", "green"))).isInstanceOf(RuntimeException.class);
	}

	@DisplayName("DB: Line 조회 테스트")
	@Test
	void findLineTest() {
		// given
		Line expected = lineRepository.save(new Line("2호선", "green"));

		// when
		Line actual = lineRepository.findById(expected.getId()).get();

		// then
		assertAll(
			() -> assertThat(actual).isNotNull(),
			() -> assertThat(actual).isEqualTo(expected)
		);
	}

	@DisplayName("DB: Line 조회 실패 테스트")
	@Test
	void findLineFailTest() {
		// given
		Line expected = lineRepository.save(new Line("2호선", "green"));

		// when
		Line actual =
			lineRepository.findById(expected.getId())
				.orElseThrow(IllegalArgumentException::new);

		// then
		assertAll(
			() -> assertThat(actual).isNotNull(),
			() -> assertThat(actual).isEqualTo(expected)
		);
	}

	@DisplayName("DB: Line 삭제 실패 테스트")
	@Test
	void deleteLineTest() {
		// given
		Line expected = lineRepository.save(new Line("2호선", "green"));

		// when // then
		assertThatThrownBy(
			() -> lineRepository.deleteById(expected.getId() + 1)
		).isInstanceOf(RuntimeException.class);
	}
}
