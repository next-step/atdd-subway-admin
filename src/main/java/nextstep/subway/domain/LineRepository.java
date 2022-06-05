package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("select l from Line l " +
            "join fetch Section s on s.line = l " +
            "where l.id = :id")
    Optional<Line> findLineById(Long id);
}
