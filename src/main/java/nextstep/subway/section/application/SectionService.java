package nextstep.subway.section.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public Section findSectionById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 구간입니다: " + id));
    }
}
