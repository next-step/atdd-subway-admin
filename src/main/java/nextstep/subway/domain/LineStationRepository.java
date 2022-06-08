package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
     @Query("select distinct s from LineStation s where s.line.id = :lineId")
     Optional<LineStation> findLineStationByLineId(Long lineId);
}
