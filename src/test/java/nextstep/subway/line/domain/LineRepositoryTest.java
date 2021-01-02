package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("LineRepository : CRUD 테스트")
@DataJpaTest
class LineRepositoryTest {

	@Autowired
	EntityManager em;

	@Autowired
	LineRepository lineRepository;

	private List<Line> savedLines;

	@BeforeEach
	void saveInitData() {
		savedLines = Arrays.asList(
			lineRepository.save(new Line("2호선", "green")),
			lineRepository.save(new Line("5호선", "purple"))
		);
		em.flush();
		em.clear();
		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@Test
	void findAll() {
		assertThat(lineRepository.findAll())
			.hasSize(savedLines.size())
			.usingElementComparator(Comparator.comparing(Line::getId))
			.containsAll(savedLines);
	}

	@Test
	void findOne() {
		Line 이호선 = savedLines.get(0);
		assertThat(lineRepository.findById(이호선.getId()))
			.isPresent()
			.map(Line::getName)
			.hasValue(이호선.getName());
	}

	@Test
	void update() {
		Line 이호선 = savedLines.get(0);

		lineRepository
			.findById(이호선.getId())
			.ifPresent(line -> line.update(new Line("3호선", "orange")));

		em.flush();
		em.clear();

		assertThat(lineRepository.findById(이호선.getId()))
			.isPresent()
			.map(Line::getName)
			.hasValue("3호선");
	}
}