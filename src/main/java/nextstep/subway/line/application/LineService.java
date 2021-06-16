package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Station upStation = findStationByStationId(request.getUpStationId());
        final Station downStation = findStationByStationId(request.getDownStationId());
        final Line newLine = Line.of(upStation, downStation, request.getName(), request.getColor(), request.getDistance());
        final Line persistLine = lineRepository.save(newLine);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(final Long id) {
        return LineResponse.of(findLineByLineId(id));
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest request) {
        final Line line = findLineByLineId(id);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        final Line line = findLineByLineId(id);
        lineRepository.delete(line);
    }

    @Transactional
    public LineResponse addSectionToLine(Long id, SectionRequest request) {
        final Station upStation = findStationByStationId(request.getUpStationId());
        final Station downStation = findStationByStationId(request.getDownStationId());
        Line line = lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        line.addSection(upStation, downStation, request.getDistance());
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional
    public LineResponse removeSectionByStationId(Long id, Long stationId) {
        Station targetStation = findStationByStationId(stationId);
        Line line = findLineByLineId(id);
        line.removeSectionByStation(targetStation);
        return LineResponse.of(lineRepository.save(line));
    }

    private Line findLineByLineId(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException(lineId));
    }

    private Station findStationByStationId(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);
    }
}
