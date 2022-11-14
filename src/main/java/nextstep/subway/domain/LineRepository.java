package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line,Long> {

    @Override
    @Query(value = "select l from Line l " +
            "join fetch l.upStation " +
            "join fetch l.downStation")
    List<Line> findAll();

    @Override
    @Query(value = "select l from Line l " +
            "join fetch l.upStation " +
            "join fetch l.downStation " +
            "where l.id = :lineId")
    Optional<Line> findById(@Param("lineId") Long lineId);
}
