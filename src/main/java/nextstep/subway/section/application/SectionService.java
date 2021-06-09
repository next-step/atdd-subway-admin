package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

	private final SectionRepository sectionRepository;

	public SectionService(final SectionRepository sectionRepository) {
		this.sectionRepository = sectionRepository;
	}

	public void registerSection(final Line line, final Station upStation, final Station downStation, final int distance) {
		Section persistSection = save(upStation, downStation, distance);
		line.addSection(persistSection);
	}

	public Section save(final Station upStation, final Station downStation, final int distance) {
		Section section = Section.of(upStation, downStation, distance);
		return this.sectionRepository.save(section);
	}
}
