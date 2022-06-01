package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.message.ErrorMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final int INDEX_MANAGE_NUMBER = 1;

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException(ErrorMessage.LINE_DUPLICATE.toMessage());
        }

        final Station upStation = getValidStation(request.getUpStationId(), ErrorMessage.LINE_NOT_VALID_UP_STATION);
        final Station downStation = getValidStation(request.getDownStationId(), ErrorMessage.LINE_NOT_VALID_DOWN_STATION);

        final Line line = lineRepository.save(new Line(
                request.getName(), request.getColor(), upStation, downStation, Distance.of(request.getDistance()))
        );

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLine() {

        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.LINE_NONE_EXIST.toMessage()));

        findLine.changeColor(lineRequest.getColor());
        findLine.changeName(lineRequest.getName());
    }

    public LineResponse findLine(Long lindId) {
        return LineResponse.of(lineRepository
                .findById(lindId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.LINE_NONE_EXIST.toMessage())));
    }

    @Transactional
    public void deleteLine(Long lindId) {
        lineRepository.deleteById(lindId);
    }


    private Station getValidStation(Long stationId, ErrorMessage msg) {
        final Optional<Station> findStation = stationRepository.findById(stationId);
        if (!findStation.isPresent()) {
            throw new IllegalArgumentException(msg.toMessage());
        }
        return findStation.get();
    }

    public Section findLineStation(Long lineId, int index) {
        return lineRepository
                .findById(lineId)
                .orElseThrow(() ->
                        new IllegalArgumentException(ErrorMessage.LINE_NONE_EXIST.toMessage()))
                .getSections()
                .get(getIndex(index));
    }

    private int getIndex(int index) {
        return index - INDEX_MANAGE_NUMBER;
    }
}
