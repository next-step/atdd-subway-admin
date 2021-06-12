package nextstep.subway.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query(value = "select s from Section s where s.upStation = :upStation and s.downStation = :downStation and s.line = :line")
    Section findIdByStationsAndLine(@Param("upStation") Station upStation,
        @Param("downStation") Station downStation,
        @Param("line") Line line);
}
