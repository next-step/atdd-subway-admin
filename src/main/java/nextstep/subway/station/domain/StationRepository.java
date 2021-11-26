package nextstep.subway.station.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StationRepository extends JpaRepository<Station, Long> {

    @Override
    List<Station> findAll();

    @Override
    List<Station> findAllById(Iterable<Long> ids);

    Optional<Station> findByName(String name);

    @Query("SELECT COUNT(s) FROM Station s WHERE s.id in :ids")
    long countAllById(@Param("ids") List<Long> ids);
}
