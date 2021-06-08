package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line saveLine = request.toLine();
        saveLine.getSections().getSection().stream()
                .forEach(
                        section -> sectionRepository.save(section)
                );
        Line persistLine = lineRepository.save(saveLine);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
            Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.updateLine(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }
}
