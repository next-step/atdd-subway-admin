package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {
	boolean existsByName(String name);

	@Query("select l from Line l join fetch l.sections.sections")
	Optional<Line> findWithSectionsById(Long aLong);
}
