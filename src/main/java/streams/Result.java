package streams;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class containing the result value and a possible throwable of a map operation
 * @param <V> the value type of the result
 */
public class Result<V> {
	V value;
	Throwable throwable;
	
	public Result(V value, Throwable throwable) {
	  this.value = value;
	  this.throwable = throwable;
	}

	public Result(V value) {
	  this.value = value;
	  this.throwable = null;
	}
	
	public V getValue() {
		return value;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}
	
	public boolean hasValue() {
		return value != null;
	}
	
	public boolean hasThrowable() {
		return throwable != null;
	}
	
	public String toString() {
		return (hasValue() ? value.toString() : "") + ((hasThrowable()) ? " [throwable message: " + throwable.getMessage() + "]": "");
	}
}