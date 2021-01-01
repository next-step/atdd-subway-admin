package nextstep.subway.line.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineRepository lineRepository;

    private final SectionFactory sectionFactory;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine(sectionFactory));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> persistLines = lineRepository.findAll();
        return LineResponse.ofList(persistLines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(final Long id) {
        Line persistLine = getPersistLine(id);
        return LineResponse.of(persistLine);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest request) {
        Line persistLine = getPersistLine(id);
        persistLine.update(request.toLine(sectionFactory));
        return LineResponse.of(persistLine);
    }

    @Transactional
    public void deleteLine(final Long id) {
        Line persistLine = getPersistLine(id);
        lineRepository.delete(persistLine);
    }

    @Transactional
    public LineResponse addSection(final Long id, final SectionRequest request) {
        Line persistLine = getPersistLine(id);
        Section section = sectionFactory.create(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
        persistLine.add(new LineStation(persistLine,section));
        return LineResponse.of(persistLine);
    }

    private Line getPersistLine(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(String.format("노선이 존재하지 않습니다. (입력 id 값: %d)", id)));
    }

    @Transactional
    public void deleteStation(final Long id, final Long stationId) {
        Line persistLine = getPersistLine(id);
        persistLine.delete(stationId);
    }
}

