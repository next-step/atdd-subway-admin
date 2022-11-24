package nextstep.subway.domain.line;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nextstep.subway.domain.station.Station;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("SELECT s FROM Section s "
		+ "WHERE (s.upStation = :upStation OR s.downStation = :downStation) "
		+ "OR (s.upStation = :downStation OR s.downStation = :upStation)")
	List<Section> findAllByStations(@Param("upStation") Station upStation, @Param("downStation") Station downStation);
}
