package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("노선 레포지토리 테스트")
@DataJpaTest
public class LineRepositoryTest {

	@Autowired
	private LineRepository lines;

	private Line 초록색_라인;
	private Line 파란색_라인;

	@BeforeEach
	void 초기화() {
		초록색_라인 = new Line("2호선", "#FFFFFF");
		파란색_라인 = new Line("4호선", "#000000");
	}

	@Test
	void 저장_영속화() {
		//given

		//when
		Line 영속화된_초록색_라인 = lines.save(초록색_라인);

		//then
		assertThat(영속화된_초록색_라인).isNotNull();
	}

	@Test
	void 목록_조회() {
		//given
		Line 영속화된_초록색_라인 = lines.save(초록색_라인);
		Line 영속화된_파란색_라인 = lines.save(파란색_라인);

		//when
		List<Line> 조회된_목록 = lines.findAll();

		//then
		assertThat(조회된_목록).contains(영속화된_초록색_라인);
		assertThat(조회된_목록).contains(영속화된_파란색_라인);
	}

	@Test
	void 조회() {
		//given
		Line 영속화된_초록색_라인 = lines.save(초록색_라인);

		//when
		Optional<Line> 조회한_초록색_라인 = lines.findById(영속화된_초록색_라인.id());

		//then
		assertThat(조회한_초록색_라인.isPresent()).isTrue();
		assertThat(영속화된_초록색_라인).isEqualTo(조회한_초록색_라인.get());
	}

	@Test
	void 수정() {
		//given
		Line 영속화된_초록색_라인 = lines.save(초록색_라인);

		//when
		영속화된_초록색_라인.update(파란색_라인);
		Optional<Line> 수정후_조회한_초록색_라인 = lines.findById(영속화된_초록색_라인.id());

		//then
		assertThat(수정후_조회한_초록색_라인.isPresent()).isTrue();
		assertThat(수정후_조회한_초록색_라인.get().name()).isEqualTo(파란색_라인.name());
	}

	@Test
	void 삭제() {
		//given
		Line 영속화된_초록색_라인 = lines.save(초록색_라인);

		//when
		lines.delete(영속화된_초록색_라인);
		Optional<Line> 삭제후_조회한_초록색_라인 = lines.findById(영속화된_초록색_라인.id());

		//then
		assertThat(삭제후_조회한_초록색_라인.isPresent()).isFalse();
	}
}
