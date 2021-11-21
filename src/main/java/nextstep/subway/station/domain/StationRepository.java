package nextstep.subway.station.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Override
    List<Station> findAll();

    List<Station> findAllByIdIsIn(List<Long> stationIds);
}