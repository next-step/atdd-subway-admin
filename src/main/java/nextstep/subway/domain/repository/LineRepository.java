package nextstep.subway.domain.repository;

import java.util.Optional;
import nextstep.subway.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findByName(String name);

    @Query(value = "select l from Line l join fetch l.sections.sections s where l.id = :lineId order by s.sortNo asc")
    Optional<Line> findWithSectionsByIdOrderBySortNoAsc(@Param("lineId") Long lineId);
}
