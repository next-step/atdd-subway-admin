package nextstep.subway.section.application;

import org.springframework.stereotype.Service;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;

@Service
public class SectionService {

	final SectionRepository repository;

	public SectionService(SectionRepository repository) {
		this.repository = repository;
	}

	public Section save(Section section) {
		return repository.save(section);
	}
}
