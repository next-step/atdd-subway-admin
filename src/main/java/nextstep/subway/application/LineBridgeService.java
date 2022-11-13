package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineBridgeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineBridgeService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public LineBridgeService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineBridgeDto.Response createLineBridge(Long id, LineBridgeDto.Request lineBridgeRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 노선을 찾지 못했습니다."));

        Station upStation = stationRepository.findById(lineBridgeRequest.getUpStationId())
                .orElseThrow(()-> new IllegalArgumentException("지하철역을 찾지 못했습니다."));

        Station downStation = stationRepository.findById(lineBridgeRequest.getDownStationId())
                .orElseThrow(()-> new IllegalArgumentException("지하철역을 찾지 못했습니다."));

        LineBridge lineBridge = new LineBridge(upStation, downStation, lineBridgeRequest.getDistance());
        line.addLineBridge(lineBridge);

        return LineBridgeDto.Response.of(lineBridge);
    }
}
