package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;

@Service
@Transactional(readOnly = true)
public class SectionService {
	private final SectionRepository sectionRepository;

	public SectionService(SectionRepository sectionRepository) {
		this.sectionRepository = sectionRepository;
	}
	
	public List<Section> findAllSections() {
		return sectionRepository.findAll();
	}
}
