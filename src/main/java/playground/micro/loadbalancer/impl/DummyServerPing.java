package playground.micro.loadbalancer.impl;

import playground.micro.loadbalancer.IServerPing;
import playground.micro.loadbalancer.Server;

public class DummyServerPing implements IServerPing {

	@Override
	public boolean isAlive(Server server) {
		return true;
	}

}
