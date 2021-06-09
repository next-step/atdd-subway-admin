package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column
	private String color;

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new LinkedList<>();

	@OneToOne
	private Station startStation;

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station startStation) {
		this.name = name;
		this.color = color;
		this.startStation = startStation;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void addSections(Section section) {
		sections.add(section);
		section.setLine(this);
	}

	public List<Station> getOrderedStations() {
		Map<Station, Station> order = getStationOrder();
		return convertToOrderList(order);
	}

	private Map<Station, Station> getStationOrder() {
		Map<Station, Station> order = new HashMap<>();

		for (Section section : sections) {
			order.put(section.getUpStation(), section.getDownStation());
		}

		return order;
	}

	private List<Station> convertToOrderList(Map<Station, Station> order) {
		List<Station> stations = new ArrayList<>();

		Station key = startStation;
		stations.add(key);

		while (order.containsKey(key)) {
			stations.add(order.get(key));
			key = order.get(key);
		}
		return stations;
	}

	public Long delete() {
		for (Section section : sections) {
			section.setLine(null);
		}

		sections = null;
		startStation = null;

		return id;
	}
}
