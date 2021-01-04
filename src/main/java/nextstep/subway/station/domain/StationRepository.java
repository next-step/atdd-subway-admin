package nextstep.subway.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface StationRepository extends JpaRepository<Station, Long> {

    @Query("select s from Station s where s.id in :ids")
    List<Station> findAllByIds(List<Long> ids);
}
