package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {
	boolean existsByName(String name);

	@Query("select l from Line l join fetch l.sections.sections where l.id = :lineId")
	Optional<Line> findWithSectionsById(@Param("lineId") Long lineId);
}
