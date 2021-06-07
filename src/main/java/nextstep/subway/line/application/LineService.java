package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository,
        final StationRepository stationRepository) {

        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        final Station station = station(request.getUpStationId());
        final Station station1 = station(request.getDownStationId());
        final Line line = request.toLine();
        final Section section = new Section(station, station1, request.getDistance(), line);
        line.addSection(section);
        final Line persistLine = lineRepository.save(line);

        return lineResponse(persistLine);
    }

    private Station station(final Long upStationId) {
        return stationRepository.findById(upStationId)
            .orElseThrow(NotFoundException::new);
    }

    private LineResponse lineResponse(final Line line) {
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        final List<Line> lines = findAllLine();
        final List<LineResponse> lineResponses = new ArrayList<>(lines.size());
        lines.forEach(line -> lineResponses.add(lineResponse(line)));

        return lineResponses;
    }

    private List<Line> findAllLine() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        return LineResponse.of(findById(id));
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    public LineResponse updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = findById(id);
        line.update(lineRequest.toLine());
        final Line savedLIne = lineRepository.save(line);

        return LineResponse.of(savedLIne);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
