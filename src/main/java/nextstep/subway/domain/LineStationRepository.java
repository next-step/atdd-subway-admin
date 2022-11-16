package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {

    @Query(value = "select ls from LineStation ls where ls.line.id = :lineId")
    Optional<List<LineStation>> findByLineId(@Param("lineId") Long lineId);
}
