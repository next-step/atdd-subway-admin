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
}
