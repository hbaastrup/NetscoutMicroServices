package playground.micro.loadbalancer.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import playground.micro.loadbalancer.ILoadBalancer;
import playground.micro.loadbalancer.IServerRule;
import playground.micro.loadbalancer.Server;

public class RandomRule implements IServerRule {
	ILoadBalancer loadBalancer;
	
	public RandomRule() {}
	
	public RandomRule(ILoadBalancer lb) {
		loadBalancer = lb;
	}

	@Override
	public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = ThreadLocalRandom.current().nextInt(serverCount);
            server = upList.get(index);

            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;
	}

	@Override
	public ILoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	@Override
	public void setLoadBalancer(ILoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

}
