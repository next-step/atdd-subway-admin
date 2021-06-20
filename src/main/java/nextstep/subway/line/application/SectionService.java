package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.exception.NoneExistLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NoneExistStationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SectionService {
	private static final String NONE_EXIST_STATION = "잘못된 역정보입니다.";
	@Autowired
	private StationRepository stationRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Line line, Long upStationId, Long downStationId, int distance) {
		Optional<Station> upStation = stationRepository.findById(upStationId);

		if (!upStation.isPresent()) {
			throw new NoneExistStationException(NONE_EXIST_STATION);
		}

		Optional<Station> downStation = stationRepository.findById(downStationId);

		if (!downStation.isPresent()) {
			throw new NoneExistStationException(NONE_EXIST_STATION);
		}

		Section newSection = Section.create(upStation.get(), downStation.get(), distance);

		line.setSection(newSection);
	}
}
