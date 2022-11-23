package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Section findByDownStation(Station station);

    Section findByUpStation(Station station);

    void deleteByLine(Line line);
}
