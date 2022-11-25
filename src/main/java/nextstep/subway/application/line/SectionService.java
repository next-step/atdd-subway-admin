package nextstep.subway.application.line;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.SectionRepository;
import nextstep.subway.domain.station.Station;

@Service
public class SectionService {

	private final SectionRepository sectionRepository;

	public SectionService(SectionRepository sectionRepository) {
		this.sectionRepository = sectionRepository;
	}

	@Transactional(readOnly = true)
	public List<Section> findSectionsToUpdate(Station upStation, Station downStation) {
		return sectionRepository.findAllByStations(upStation, downStation);
	}
}
