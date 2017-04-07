package ddba.strategy;

/**
 * Created by ddba on 07/04/2017.
 */
public class StrategyA implements Strategy{

	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public Context execute() {
		return null;
	}
}
