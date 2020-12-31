package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.distance.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        final Station upStation = findStationById(request.getUpStationId());
        final Station downStation = findStationById(request.getDownStationId());

        final Line persistLine = lineRepository.save(request.toLineWithSection(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    private Station findStationById(final long id) {
        return stationRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final long id) {
        return LineResponse.of(lineRepository.findById(id)
            .orElseThrow(NotFoundException::new));
    }

    @Transactional
    public LineResponse updateLine(final long id, final Line line) {
        final Line foundLine = lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);

        foundLine.update(line);
        return LineResponse.of(foundLine);
    }

    @Transactional
    public void deleteLine(final long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse addSection(final Long lineId, final LineRequest request) {
        final Station upStation = findStationById(request.getUpStationId());
        final Station downStation = findStationById(request.getDownStationId());

        final Line foundLine = lineRepository.findById(lineId)
            .orElseThrow(NotFoundException::new);

        foundLine.addSection(new Section(foundLine, upStation, downStation, new Distance(request.getDistance())));

        return LineResponse.of(foundLine);
    }
}
