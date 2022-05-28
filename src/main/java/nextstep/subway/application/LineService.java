package nextstep.subway.application;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Line line = lineRequest.toLine();
        Station upStation = getStationById(lineRequest.getUpStationId());
        Station downStation = getStationById(lineRequest.getDownStationId());

        Section section = Section.create(upStation, downStation, lineRequest.getDistance());

        line.addSection(section);
        line.setTerminus(upStation, downStation);

        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private Station getStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철역입니다. stationId : " + stationId));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = getLineById(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(Long lineId, LineRequest request) {
        Line line = getLineById(lineId);

        line.modifyNameAndColor(request.getName(), request.getColor());
    }

    private Line getLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다. lineId : " + lineId));
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
