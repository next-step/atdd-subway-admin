package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        Line line = findLineById(lineId);
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line persistLine = findLineById(lineId);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        persistLine.addSection(sectionRequest.getDistance(), upStation, downStation);
        return LineResponse.from(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = findLineById(lineId);
        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::from).collect(Collectors.toList());
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
    }

    private Station findStationByIdOrElseNull(Long stationId) {
        return stationRepository.findById(stationId).orElse(null);
    }

    public void deleteSection(long id, long stationId) {
        Line persistLine = findLineById(id);
        Station station = findStationById(stationId);
        persistLine.deleteSection(station);
    }
}
