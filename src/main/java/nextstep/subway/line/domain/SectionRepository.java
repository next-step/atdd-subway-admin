package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.station.domain.Station;

public interface SectionRepository extends JpaRepository<Section, Long> {
	Optional<Section> findByLineAndUpAndDown(final Line line, final Station up, final Station down);
}
