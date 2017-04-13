package ddba.strategy;

import ddba.Output;
import ddba.Tuple;

import java.util.List;

public interface Strategy {
	boolean canExecute(Context context);
	Tuple<Context,List<Output>> execute(Context context);

}
