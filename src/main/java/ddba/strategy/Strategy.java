package ddba.strategy;

import ddba.Tuple;

/**
 * Created by ddba on 07/04/2017.
 */
public interface Strategy {
	boolean canExecute(Context context);
	Tuple execute(Context context);

}
