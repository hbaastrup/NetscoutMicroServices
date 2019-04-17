package playground.micro.loadbalancer.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import playground.micro.loadbalancer.ILoadBalancer;
import playground.micro.loadbalancer.IServerRule;
import playground.micro.loadbalancer.Server;

public class RoundRobinRule implements IServerRule {
	private static Logger LOG = LoggerFactory.getLogger(RoundRobinRule.class);
	
	private AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);
	ILoadBalancer loadBalancer = null;
	
	public RoundRobinRule() {}
	
	public RoundRobinRule(ILoadBalancer lb) {
		loadBalancer = lb;
	}
	
	@Override
	public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
        	LOG.warn("no load balancer");
            return null;
        }

        Server server = null;
        int count = 0;
        while (server == null && count++ < 10) {
            List<Server> reachableServers = lb.getReachableServers();
            List<Server> allServers = lb.getAllServers();
            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            if ((upCount == 0) || (serverCount == 0)) {
            	LOG.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            server = allServers.get(nextServerIndex);

            if (server == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }

            if (server.isAlive() && (server.isReadyToServe())) {
                return (server);
            }

            // Next.
            server = null;
        }

        if (count >= 10) {
        	LOG.warn("No available alive servers after 10 tries from load balancer: "+ lb);
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

	
	
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }

}
