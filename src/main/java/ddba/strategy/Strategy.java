package ddba.strategy;

import ddba.Output;
import ddba.Tuple;

import java.util.LinkedList;

/**
 * Created by ddba on 07/04/2017.
 */
public interface Strategy {
	boolean canExecute(Context context);
	Tuple<Context,LinkedList<Output>> execute(Context context);

}
