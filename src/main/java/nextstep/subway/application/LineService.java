package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, final LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        final Line line = lineRequest.toLine();
        final Section section = new Section(lineRequest.getDistance())
                .updateUpStationBy(stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(EntityNotFoundException::new))
                .updateDownStationBy(stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(EntityNotFoundException::new));
        return LineResponse.of(lineStationRepository.save(new LineStation(line, section)));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        final Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest updateLine) {
        final Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        line.updateBy(updateLine);
    }
}
