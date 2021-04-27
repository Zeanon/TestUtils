package de.zeanon.testutils.regionsystem.tags;


import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public enum Tag {

	CHANGED(de.zeanon.testutils.regionsystem.tags.tagvalues.CHANGED.class, de.zeanon.testutils.regionsystem.tags.tagvalues.CHANGED.FALSE);


	@Getter
	private static final @NotNull Set<Tag> tags;

	static {
		tags = EnumSet.allOf(Tag.class);
	}


	private final @NotNull Class<? extends Value<?>> valueType;
	private final @NotNull Tag.Value<?> defaultValue;
	private final Value<?>[] values;


	<T extends Enum<T> & Value<T>> Tag(final @NotNull Class<? extends Value<T>> valueType, final @NotNull Tag.Value<T> defaultValue) {
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.values = defaultValue.getValues();
	}


	public Value<?> getTagValueOf(final @NotNull String name) {
		return this.defaultValue.getValueOf(name);
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}


	public interface Value<T extends Enum<T> & Value<T>> {

		T getValue();

		T getValueOf(final @NotNull String name);

		@NotNull T[] getValues();

		@NotNull String getChatValue();

		@NotNull String getDescription();

		default @NotNull String getName() {
			return this.getValue().name().toLowerCase();
		}
	}
}