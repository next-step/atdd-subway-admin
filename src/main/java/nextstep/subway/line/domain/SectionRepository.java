package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByLine(Line line);

    @Query("SELECT s FROM Section s WHERE (s.upStation = :upStation OR s.downStation = :downStation) "
                  + "OR (s.upStation = :downStation OR s.downStation = :upStation)")
    List<Section> findAllByRequestedSection(@Param("upStation") Station upStation, @Param("downStation") Station downStation);

    Optional<Section> findByUpStationId(Long upStation);
    Optional<Section> findByDownStationId(Long downStation);
}
