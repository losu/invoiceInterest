package ddba.strategy;

/**
 * Created by ddba on 07/04/2017.
 */
public interface Strategy {
	boolean canExecute();
	Context execute();

}
