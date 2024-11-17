package bg.sofia.uni.fmi.mjt.olympics.competitor;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Athlete implements Competitor {

	private final String identifier;
	private final String name;
	private final String nationality;

	private final Map<Medal, Integer> medals;

	public Athlete(String identifier, String name, String nationality) {
		if (identifier == null || identifier.isBlank()) {
			throw new IllegalArgumentException("Identifier cannot be null or blank");
		}
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name cannot be null or blank");
		}
		if (nationality == null || nationality.isBlank()) {
			throw new IllegalArgumentException("Nationality cannot be null or blank");
		}

		this.identifier = identifier;
		this.name = name;
		this.nationality = nationality;

		this.medals = new EnumMap<>(Medal.class);
		for (Medal medal : Medal.values()) {
			medals.put(medal, 0);
		}
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNationality() {
		return nationality;
	}

	@Override
	public Collection<Medal> getMedals() {
		return Collections.unmodifiableCollection(
				medals.entrySet()
						.stream()
						.flatMap(entry -> Collections.nCopies(entry.getValue(),
								 entry.getKey()).stream())
						.toList()
		);
	}

	@Override
	public void addMedal(Medal medal) {
		if (medal == null) {
			throw new IllegalArgumentException("Medal cannot be null");
		}
		medals.put(medal, medals.get(medal) + 1);
	}

	public int getMedalCount(Medal medal) {
		if (medal == null) {
			throw new IllegalArgumentException("Medal cannot be null");
		}
		return medals.getOrDefault(medal, 0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Athlete athlete = (Athlete) o;
		return Objects.equals(identifier, athlete.identifier) &&
				Objects.equals(name, athlete.name) &&
				Objects.equals(nationality, athlete.nationality) &&
				Objects.equals(medals, athlete.medals);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, name, nationality, medals);
	}

	@Override
	public String toString() {
		return "Athlete{" +
				"identifier='" + identifier + '\'' +
				", name='" + name + '\'' +
				", nationality='" + nationality + '\'' +
				", medals=" + medals +
				'}';
	}
}