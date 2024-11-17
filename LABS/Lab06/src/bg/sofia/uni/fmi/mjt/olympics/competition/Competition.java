package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a competition with a unique name, discipline, and set of competitors.
 *
 * @throws IllegalArgumentException when creating a competition with null or blank name
 * @throws IllegalArgumentException when creating a competition with null or blank discipline
 * @throws IllegalArgumentException when creating a competition with null or empty competitors
 */
public record Competition(String name, String discipline, Set<Competitor> competitors) {

	public Competition {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Competition name cannot be null or blank");
		}
		if (discipline == null || discipline.isBlank()) {
			throw new IllegalArgumentException("Competition discipline cannot be null or blank");
		}
		if (competitors == null || competitors.isEmpty()) {
			throw new IllegalArgumentException("Competitors set cannot be null");
		}
		for (Competitor competitor : competitors) {
			if (competitor == null) {
				throw new IllegalArgumentException("Competitors set cannot contain null elements");
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getDiscipline() {
		return discipline;
	}

	public Set<Competitor> competitors() {
		return Collections.unmodifiableSet(competitors);
	}

	@Override
	public boolean equals(Object o) {

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		if (this == o) {
			return true;
		}

		Competition that = (Competition) o;
		return name.equals(that.name) && discipline.equals(that.discipline);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, discipline);
	}

	@Override
	public String toString() {
		return "Competition{" +
				"name='" + name + '\'' +
				", discipline='" + discipline + '\'' +
				", competitors=" + competitors +
				'}';
	}
}