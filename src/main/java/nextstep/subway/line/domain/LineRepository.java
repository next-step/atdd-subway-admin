package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

	// compare for (nextstep.subway.line 구간 추가 기능 7 tests)
	// before(default findById, lazy-loading): 5 sec 789 ms ~ 6 sec 110 ms
	// after(fetch join): 4 sec 215 ms ~ 4 sec 591 ms
	@Query("SELECT DISTINCT l FROM Line l"
		+ " JOIN FETCH l.sections w"
		+ " JOIN FETCH w.sections sc"
		+ " JOIN FETCH sc.upStation"
		+ " JOIN FETCH sc.downStation"
	)
	Optional<Line> findById(Long id);
}
