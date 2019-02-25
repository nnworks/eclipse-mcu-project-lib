package streams;

import java.util.function.Function;
import java.util.function.Predicate;

public class ResultLambdas {
	
	public static enum Filter {
		SUCCESS,
		EXCEPTIONALS
	}
	
	/**
	 * Creates a filter to remove the success or the exceptional results.
	 * @param filterExceptional the result to filter: when true, the exceptional results are filtered out, else the success results.
	 * @return true or false depending filterExceptional given.
	 */
	public static <V> Predicate<Result<V>> createResultFilter(Filter filter) {
		switch (filter) {
		    case SUCCESS:
				return (result) -> {
					if (!result.hasThrowable()) {
						return false;
					} else {
						return true;
					}
				};
		    case EXCEPTIONALS:
				return (result) -> {
					if (result.hasThrowable()) {
						return false;
					} else {
						return true;
					}
				};
			default:
				throw new IllegalArgumentException("Did not expect value " + filter + " for the filter");
		}
	}
	
	/**
	 * creates a lambda function that calls the success function when the result is not exceptional, or the exceptional function when it is. 
	 * @param success function called on success, with the result as parameter
	 * @param exceptional function called in case the result has a throwable attached, with the result as parameter
	 * @return the result of the success or exceptional call
	 */
	public static <V> Function<Result<V>, Result<V>> createHandler(Function<Result<V>, Result<V>> success, Function<Result<V>, Result<V>> exceptional) {
		return (p) -> {
			if (!p.hasThrowable()) {
				return success.apply(p);
			} else {
				return exceptional.apply(p);
			}
		};
	}

}
