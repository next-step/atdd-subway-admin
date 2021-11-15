package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l " +
            "left join fetch l.sections.sections sec " +
            "left join fetch sec.downStation " +
            "left join fetch sec.upStation " +
            "where l.id = :id")
    Optional<Line> findOneWithStations(@Param("id") Long id);

    @Query("select l from Line l " +
            "left join fetch l.sections.sections sec " +
            "left join fetch sec.downStation " +
            "left join fetch sec.upStation")
    List<Line> findAllWithStations();
}
