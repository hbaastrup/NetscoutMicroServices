package playground.micro.loadbalancer;

/**
 * Interface that defines how we "ping" a server to check if its alive
 * 
 * @author hbaastrup
 *
 */
public interface IServerPing {

    /**
     * Checks whether the given <code>Server</code> is "alive" i.e. should be
     * considered a candidate while loadbalancing
     * 
     */
    public boolean isAlive(Server server);
}
