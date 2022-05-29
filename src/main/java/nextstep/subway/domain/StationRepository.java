package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();
    @Query("SELECT s FROM Station s WHERE s.lineId = :lineId")
    List<Station> findAllByLineId(@Param(value = "lineId")Long lineId);
}