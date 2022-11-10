package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line,Long> {

    @Override
    @Query(value = "select l from Line l " +
            "join fetch l.upStation " +
            "join fetch l.downStation")
    List<Line> findAll();
}
