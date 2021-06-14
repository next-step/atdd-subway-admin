package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Section findByLineAndDownStation(Line line, Station downStation);

    List<Section> findByLine(Line line);
}
