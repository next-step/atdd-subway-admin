package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select DISTINCT line from Line line join fetch line.stations.stations")
    List<Line> findAll();

    @Override
    @Query("select DISTINCT line from Line line join fetch line.stations.stations")
    Optional<Line> findById(Long aLong);
}
