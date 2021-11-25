package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {

	@Query("SELECT l FROM Line l"
		+ " JOIN FETCH l.sections w"
		+ " JOIN FETCH w.sections sc"
		+ " JOIN FETCH sc.upStation"
		+ " JOIN FETCH sc.downStation"
		+ " WHERE l.id = :id"
	)
	Optional<Line> findById(@Param("id") Long id);
}
