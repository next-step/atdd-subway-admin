package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.DuplicateLineException;
import nextstep.subway.line.exception.NoSuchLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NoSuchStationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        checkLineExistence(request.getName());
        Line line = request.toLine();

        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        line.addSection(new Section(upStation, downStation, request.getDistance()));
        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private Station getStationById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NoSuchStationException("해당 지하철역이 존재하지 않습니다."));
    }

    private void checkLineExistence(String name) {
        lineRepository.findByName(name)
            .ifPresent(line -> {
                throw new DuplicateLineException("해당 노선이름이 이미 존재합니다.");
            });
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = getLineById(id);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getLineById(id);
        line.update(lineRequest.toLine());
    }

    public void deleteLine(Long id) {
        Line line = getLineById(id);
        lineRepository.delete(line);
    }

    private Line getLineById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NoSuchLineException("존재하지 않는 노선 ID 입니다."));
    }

    public LineResponse saveSection(Long lineId, SectionRequest request) {
        Line line = getLineById(lineId);

        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        line.addSection(new Section(upStation, downStation, request.getDistance()));
        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);

    }
}
