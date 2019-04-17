package playground.micro.loadbalancer;

public interface IServerRule {
	
	public Server choose(ILoadBalancer lb, Object key);
	public ILoadBalancer getLoadBalancer();
	public void setLoadBalancer(ILoadBalancer loadBalancer);
}
