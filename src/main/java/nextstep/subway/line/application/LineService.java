package nextstep.subway.line.application;

import static nextstep.subway.common.exception.ErrorMessage.NO_EXISTS_LINE_ERROR;
import static nextstep.subway.common.exception.ErrorMessage.NO_EXISTS_STATION_ERROR;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = mapToSection(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        Line save = lineRepository.save(Line.of(lineRequest.getName(), lineRequest.getColor(), section));
        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(findById(id));
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Section mapToSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        return Section.of(upStation, downStation, Distance.from(distance));
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_EXISTS_LINE_ERROR.getMessage()));
    }

    @Transactional
    public LineResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = findById(id);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        line.addSection(Section.of(upStation, downStation, Distance.from(sectionRequest.getDistance())));
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse removeSection(Long lineId, Long stationId) {
        Line line = findById(lineId);
        Station station = findStationById(stationId);
        line.remove(station);
        return LineResponse.of(line);
    }

    public Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_EXISTS_STATION_ERROR.getMessage()));
    }
}
