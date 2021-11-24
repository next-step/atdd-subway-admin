package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {

	@Override
	@Query("select distinct l from Line l join fetch l.sections s join fetch s.upStation join fetch s.downStation where l.id = :lineId")
	Optional<Line> findById(@Param("lineId") Long lineId);

	@Override
	@Query("select distinct l from Line l join fetch l.sections s join fetch s.upStation join fetch s.downStation")
	List<Line> findAll();
}
