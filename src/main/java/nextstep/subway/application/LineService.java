package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("중복된 노선 입니다");
        }

        final Station upStation = getValidStation(request.getUpStationId(), "유효하지 않은 상행종점입니다.");
        final Station downStation = getValidStation(request.getDownStationId(), "유효하지 않은 하행종점입니다.");

        final Line line = lineRepository.save(
            request.toLine().withUpStation(upStation).withDownStation(downStation)
        );

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않은 노선입니다."));

        findLine.changeColor(lineRequest.getColor());
        findLine.changeName(lineRequest.getName());
        lineRepository.flush();
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lindId) {
        return LineResponse.of(lineRepository
                .findById(lindId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않은 노선입니다.")));
    }

    public void deleteLine(Long lindId) {
        lineRepository.deleteById(lindId);
    }


    private Station getValidStation(Long stationId, String msg) {
        final Optional<Station> findStation = stationRepository.findById(stationId);
        if (!findStation.isPresent()) {
            throw new IllegalArgumentException(msg);
        }
        return findStation.get();
    }
}
