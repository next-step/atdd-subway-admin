package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class SectionService {

	private final SectionRepository sectionRepository;

	public SectionService(SectionRepository sectionRepository) {
		this.sectionRepository = sectionRepository;
	}

	public List<Section> getTerminalSections(Station upStation, Station downStation, int distance) {
		return new ArrayList<Section>() {{
			add(getUpTerminalSection(upStation));
			add(getSection(upStation, downStation, distance));
			add(getDownTerminalSection(downStation));
		}};
	}

	private Section getSection(Station upStation, Station downStation, int distance) {
		return sectionRepository.findByUpStationAndDownStation(upStation, downStation)
			.orElse(new Section(upStation, downStation, distance));
	}

	private Section getUpTerminalSection(Station station) {
		return sectionRepository.findByUpStationIsNullAndDownStation(station)
			.orElse(new Section(null, station));
	}

	private Section getDownTerminalSection(Station station) {
		return sectionRepository.findByUpStationAndDownStationIsNull(station)
			.orElse(new Section(station, null));
	}
}
