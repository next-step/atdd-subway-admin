package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.station.domain.Station;

public interface SectionRepository extends JpaRepository<Section, Long> {
	Optional<Section> findByUpStationIsNullAndDownStation(Station downStation);
	Optional<Section> findByUpStationAndDownStationIsNull(Station upStation);
	Optional<Section> findByUpStationAndDownStation(Station upStation, Station downStation);
}
