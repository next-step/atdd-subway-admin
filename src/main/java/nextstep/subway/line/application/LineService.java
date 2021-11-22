package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.UpdateLineResponseDto;
import nextstep.subway.line.exception.DuplicateLineNameException;
import nextstep.subway.line.exception.NotFoundLineByIdException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineCreateResponse saveLine(LineRequest lineRequest) {
        validateExistsByName(lineRequest.getName());
        final Section section = sectionService.getSectionOrElseThrow(lineRequest.getSectionRequest());
        final Line persistLine = lineRepository.save(lineRequest.toLine(section));
        return LineCreateResponse.of(persistLine);
    }

    @Transactional
    public UpdateLineResponseDto updateLine(long id, LineRequest lineRequest) {
        validateExistsByName(lineRequest.getName());
        final Line line = getLineByIdOrElseThrow(id);
        final Section section = sectionService.getSectionOrElseThrow(lineRequest.getSectionRequest());
        line.update(lineRequest.toLine(section));
        lineRepository.flush();
        return UpdateLineResponseDto.of(line);
    }

    private void validateExistsByName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new DuplicateLineNameException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(long id) {
        final Line line = getLineByIdOrElseThrow(id);
        return LineResponse.of(line);
    }

    private Line getLineByIdOrElseThrow(long id) {
        return lineRepository.findById(id)
                .orElseThrow(NotFoundLineByIdException::new);
    }

    @Transactional
    public void deleteLine(long id) {
        try {
            lineRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundLineByIdException();
        }
    }
}
