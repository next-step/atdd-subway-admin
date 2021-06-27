package nextstep.subway.line.application;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(request.toLine());
        Section section = sectionService.saveSectionWith(line, request);
        line.add(section);

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineBy(Long id) {
        return lineRepository
            .findById(id)
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public LineResponse updateLineBy(Long id, LineRequest lineRequest) {
        return lineRepository
            .findById(id)
            .map(it -> it.getUpdatedLineBy(lineRequest))
            .map(LineResponse::of)
            .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteStationBy(Long id) {
        lineRepository.deleteById(id);
    }
}
