package nextstep.subway.station.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
	private StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse saveStation(StationRequest stationRequest) {
		Station persistStation = stationRepository.save(stationRequest.toStation());
		return StationResponse.of(persistStation);
	}

	/**
	 * 애플리케이션 계층:
	 * - 해야 할 작업을 정의(작업 조율), 작업의 진행상태 나타내는 정보는 가질 수 있다.
	 * - 도메인 객체들에게 작업을 위임한다.
	 * - 비즈니스적인 관점에서 의미가 있거나, 타 시스템의 애플리케이션 계층과 연동하기 위한 것이다.
	 * - 이 계층은 얇게 유지되어야 한다.
	 * - 업무 로직이나 지식을 가지고 있지 않는다
	 */

	@Transactional(readOnly = true)
	public List<StationResponse> findAllStations() {
		List<Station> stations = stationRepository.findAll();

		return stations.stream()
				.map(station -> StationResponse.of(station))
				.collect(Collectors.toList());
	}

	public StationResponse findStationById(Long id) {
		Station station = stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 역이 없습니다 id=" + id));
		return StationResponse.of(station);
	}

	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}
}
