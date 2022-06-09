package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.StationNotFoundException;

@Service
@Transactional(readOnly = true)
public class SectionService {
	private final StationRepository stationRepository;
	private final SectionRepository sectionRepository;

	public SectionService(StationRepository stationRepository, SectionRepository sectionRepository) {
		this.stationRepository = stationRepository;
		this.sectionRepository = sectionRepository;
	}

	public List<Section> findAllSections() {
		return sectionRepository.findAll();
	}

	public Section save(LineRequest lineRequest) {
		Station upStation = stationRepository.findById(lineRequest.getUpStationId())
				.orElseThrow(() -> new StationNotFoundException());
		Station downStation = stationRepository.findById(lineRequest.getDownStationId())
				.orElseThrow(() -> new StationNotFoundException());

		return sectionRepository.save(lineRequest.toSection(upStation, downStation));
	}

	public Section save(SectionRequest sectionRequest) {
		Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
				.orElseThrow(() -> new StationNotFoundException());
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
				.orElseThrow(() -> new StationNotFoundException());

		return sectionRepository.save(sectionRequest.toSection(upStation, downStation));
	}
}
