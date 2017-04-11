package ddba.strategy.strategies;

import ddba.Output;
import ddba.Tuple;
import ddba.strategy.Context;
import ddba.strategy.Strategy;

import java.util.LinkedList;

/**
 * Created by ddba on 11/04/2017.
 */
public class StrategyZ implements Strategy {

	@Override
	public boolean canExecute(Context context) {
		return false;
	}

	@Override
	public Tuple<Context, LinkedList<Output>> execute(Context context) {
		return null;
	}
}
