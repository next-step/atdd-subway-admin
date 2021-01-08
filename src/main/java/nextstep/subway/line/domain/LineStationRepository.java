package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    @EntityGraph(attributePaths = {"station", "previousStation", "nextStation"})
    @Query("select ls from LineStation ls"
            + " where ls.line = :line"
            + " and (ls.station = :station or ls.previousStation = :previousStation or nextStation = :nextStation)")
    List<LineStation> findLineStations(@Param("line") Line line,
                                       @Param("station") Station station,
                                       @Param("previousStation") Station previousStation,
                                       @Param("nextStation") Station nextStation);
}
