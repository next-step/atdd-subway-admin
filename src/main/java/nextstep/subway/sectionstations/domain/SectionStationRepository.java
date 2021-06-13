package nextstep.subway.sectionstations.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionStationRepository extends JpaRepository<SectionStation, Long> {
    @Query("select s.station from SectionStation s where s.section.id in (:ids)")
    List<Station> findStationsBySection(List<Long> ids);
}
