package nextstep.subway.station.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    @Query("select s from Station s where s.id in (:ids)")
    List<Station> findStations(List<Long> ids);
}