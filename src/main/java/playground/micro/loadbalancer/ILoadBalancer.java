package playground.micro.loadbalancer;

import java.util.List;

public interface ILoadBalancer {
	public List<Server> getAllServers();
	public List<Server> getReachableServers();
	public Server choose();
}
