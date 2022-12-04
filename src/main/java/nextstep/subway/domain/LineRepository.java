package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select distinct l from Line l"
        + " join fetch l.sections.sections s")
    List<Line> findAllLines();

    @Query("select distinct l from Line l"
        + " join fetch l.sections.sections s"
        + " where l.id = :id")
    Optional<Line> findLine(@Param("id") Long id);

}
