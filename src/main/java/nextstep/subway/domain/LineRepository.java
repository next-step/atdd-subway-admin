package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("SELECT DISTINCT l FROM Line l LEFT JOIN FETCH l.sections")
    List<Line> findAll();
    @Query("SELECT DISTINCT l FROM Line l LEFT JOIN FETCH l.sections")
    Optional<Line> findById(Long id);
}
