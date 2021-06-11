package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l join fetch l.sections.sections where l.id = :id")
    Optional<Line> findByIdFetch(@Param("id") Long id);
}
