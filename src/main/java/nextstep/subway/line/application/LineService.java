package nextstep.subway.line.application;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.List;
import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> readAllLines() {
        final List<Line> lines = lineRepository.findAllWithSections();
        return LineResponse.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse readLine(Long id) {
        final Line line = readById(id);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        final Line line = readById(id);
        line.update(lineRequest.toLineForUpdate());
        return LineResponse.of(line);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = readWithSectionsById(lineId);
        line.addSection(sectionRequest.toSectionWith(line));

        List<Long> order = line.getStationIds();
        List<Station> stations = stationRepository.findAllById(order);

        return LineResponse.of(line, stations.stream()
            .sorted(comparing(station -> order
                .indexOf(station.getId())))
            .collect(toList()));
    }

    private Line readById(Long id) {
        return lineRepository.findById(id)
                             .orElseThrow(() -> new NotFoundException("해당하는 Line이 없습니다. id = " + id));
    }

    private Line readWithSectionsById(Long id) {
        return lineRepository.findWithSectionsById(id)
            .orElseThrow(() -> new NotFoundException("해당하는 Line이 없습니다. id = " + id));
    }
}
