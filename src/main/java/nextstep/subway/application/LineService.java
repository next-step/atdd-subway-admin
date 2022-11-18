package nextstep.subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;

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
        Line line = lineRepository.save(toLineBy(lineRequest));
        return getLineResponseBy(line);
    }

    public List<LineResponse> findAllLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lineRepository.findAll()) {
            lineResponses.add(getLineResponseBy(line));
        }
        return lineResponses;
    }

    public LineResponse findLine(Long lineId) {
        Line line = getLineBy(lineId);
        return getLineResponseBy(line);
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest lineRequest) {
        Line line = getLineBy(lineId);
        line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line getLineBy(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("지하철 노선 아이디 [ " + lineId + " ]를 찾을 수 없습니다."));
    }

    private Station getLastStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("지하철 역 아이디 [ " + stationId + " ]를 찾을 수 없습니다."));
    }


    private Line toLineBy(LineRequest lineRequest) {
        Station lastUpStation = getLastStation(lineRequest.getUpStationId());
        Station lastDownStation = getLastStation(lineRequest.getDownStationId());
        return new Line(lineRequest.getName(),
                lineRequest.getColor(),
                lastUpStation,
                lastDownStation,
                lineRequest.getDistance()
        );
    }

    private LineResponse getLineResponseBy(Line line) {
        List<StationResponse> stations = line.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stations
        );
    }
}
