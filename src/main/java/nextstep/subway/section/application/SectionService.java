package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.exception.NoneExistLineException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SectionService {
	@Autowired
	private StationRepository stationRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Line line, Long upStationId, Long downStationId, int distance) {
		Optional<Station> upStation = stationRepository.findById(upStationId);
		if (!upStation.isPresent()) {
			throw new NoneExistLineException("잘못된 상행선 정보입니다.");
		}

		Optional<Station> downStation = stationRepository.findById(downStationId);
		if (!downStation.isPresent()) {
			throw new NoneExistLineException("잘못된 하행선 정보입니다.");
		}

		Section newSection = Section.create(upStation.get(), downStation.get(), distance);

		line.setSection(newSection);
	}
}
