package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.response.LineResponse;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(
            StationNotFoundException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(
                StationNotFoundException::new);

        Line savedLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLineById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        Station upStation = getStationSafely(lineRequest.getUpStationId());
        Station downStation = getStationSafely(lineRequest.getDownStationId());
        Line lineUpdate = lineRequest.toLine(upStation, downStation);
        line.update(lineUpdate);
    }

    private Station getStationSafely(Long stationId) {
        if (ObjectUtils.isEmpty(stationId)) {
            return null;
        }
        return stationRepository.findById(stationId).orElse(null);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
