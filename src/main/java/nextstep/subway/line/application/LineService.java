package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다. ";

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Section section = createSection(request);
        final Line line = request.toLine();
        line.addSection(section);
        final Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Section createSection(final LineRequest request) {
        final Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(EntityExistsException::new);
        final Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(EntityExistsException::new);
        return new Section(upStation, downStation, request.getDistance());
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(toList());
    }

    public LineResponse findLineById(final Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id)));
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id));
        line.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id));
        lineRepository.delete(line);
    }
}
