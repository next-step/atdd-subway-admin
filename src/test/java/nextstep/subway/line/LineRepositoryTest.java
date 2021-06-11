package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;

@DataJpaTest
public class LineRepositoryTest {

	@Autowired
	LineRepository lines;

	Line firstLine;

	@BeforeEach
	void 지하철_노선_생성() {
		firstLine = new Line("1호선", "Blue");
		lines.save(firstLine);
	}

	@Test
	void 지하철_노선_조회() {
		Line findLine = lines.findById(firstLine.getId()).get();
		assertThat(findLine.getColor()).isEqualTo(firstLine.getColor());
		assertThat(findLine.getName()).isEqualTo(firstLine.getName());
	}

	@Test
	void 지하철_노선_삭제() {
		lines.deleteById(firstLine.getId());
		assertThatThrownBy(
			() -> lines.findById(firstLine.getId()).orElseThrow(EntityExistsException::new)
		).isInstanceOf(EntityExistsException.class);
	}

}
