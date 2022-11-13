package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineService lineService;
    private final SectionRepository sectionRepository;

    public SectionService(LineService lineService, SectionRepository sectionRepository) {
        this.lineService = lineService;
        this.sectionRepository = sectionRepository;
    }

    public List<SectionResponse> findByLine(Long lineId) {
        Line line = lineService.findById(lineId);

        return sectionRepository.findByLine(line).stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
