package playground.micro.loadbalancer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import playground.micro.loadbalancer.ILoadBalancer;
import playground.micro.loadbalancer.IServerPing;
import playground.micro.loadbalancer.IServerRule;
import playground.micro.loadbalancer.Server;
import playground.micro.loadbalancer.ShutdownEnabledTimer;

public class BaseLoadBalancer implements ILoadBalancer {
	private static Logger LOG = LoggerFactory.getLogger(BaseLoadBalancer.class);
	
	protected volatile List<Server> fullServerList = Collections.synchronizedList(new ArrayList<Server>());
	protected volatile List<Server> upServerList = Collections.synchronizedList(new ArrayList<Server>());

	protected ReadWriteLock fullServerLock = new ReentrantReadWriteLock();
	protected ReadWriteLock upServerLock = new ReentrantReadWriteLock();
	
	IServerRule serverRule;
	IServerPing serverPing;
	
	protected String name = "default";
	
	protected Timer lbTimer = null;
	protected int pingIntervalSeconds = 10;
	
	protected AtomicBoolean pingInProgress = new AtomicBoolean(false);
	
	
	
	public BaseLoadBalancer(IServerRule rule, IServerPing ping) {
		setRule(rule);
		setPing(ping);
	}
	
    public void setRule(IServerRule rule) {
        if (rule != null) {
            this.serverRule = rule;
        } else {
            /* default rule */
            this.serverRule = new RoundRobinRule();
        }
        if (this.serverRule.getLoadBalancer() != this) {
            this.serverRule.setLoadBalancer(this);
        }
    }

    public void setPing(IServerPing ping) {
        if (ping != null) {
            if (!ping.equals(this.serverPing)) {
                this.serverPing = ping;
                setupPingTask(); // since ping data changed
            }
        } else {
            this.serverPing = null;
            // cancel the timer task
            lbTimer.cancel();
        }
    }
    
    @Override
    public List<Server> getReachableServers() {
        return Collections.unmodifiableList(upServerList);
    }

    @Override
    public List<Server> getAllServers() {
        return Collections.unmodifiableList(fullServerList);
    }
    
    @Override
    public Server choose() {
    	return serverRule.choose(this, null);
    }

    
    
    
    
    private  void setupPingTask() {
        if (canSkipPing()) {
            return;
        }
        if (lbTimer != null) {
            lbTimer.cancel();
        }
        lbTimer = new ShutdownEnabledTimer("NFLoadBalancer-PingTimer-" + name, true);
        lbTimer.schedule(new PingTask(), 0, pingIntervalSeconds * 1000);
        
        // Force an immediate ping
        new PingTask().run();
    }


    
    private boolean canSkipPing() {
        if (serverPing == null
                || serverPing.getClass().getName().equals(DummyServerPing.class.getName())) {
            // default ping, no need to set up timer
            return true;
        } else {
            return false;
        }
    }


    
    
    
    
    
    /**
     * TimerTask that keeps runs every X seconds to check the status of each
     * server/node in the Server List
     * 
     * @author stonse
     * 
     */
    class PingTask extends TimerTask {
        public void run() {
            if (!pingInProgress.compareAndSet(false, true))
                return; // Ping in progress - nothing to do
            
            Server[] allServers = null;
            boolean[] pingResults = null;
            Lock fullLock = null;
            Lock upLock = null;

            try {
            	fullLock = fullServerLock.readLock();
            	fullLock.lock();
                allServers = fullServerList.toArray(new Server[fullServerList.size()]);
                fullLock.unlock();
                
                pingResults = new boolean[allServers.length];
                for (int i=0; i<allServers.length; i++) {
                	pingResults[i] = serverPing.isAlive(allServers[i]);
                }
                
                List<Server> newUpList = new ArrayList<Server>();
                List<Server> changedServers = new ArrayList<Server>();
                
                for (int i=0; i<allServers.length; i++) {
                	boolean oldIsAlive = allServers[i].isAlive();
                	allServers[i].setAlive(pingResults[i]);
                	
                	if (oldIsAlive != pingResults[i]) {
                		changedServers.add(allServers[i]);
                	}
                	
                	if (pingResults[i]) {
                        newUpList.add(allServers[i]);
                    }
                }
                upLock = upServerLock.writeLock();
                upLock.lock();
                upServerList = newUpList;
                upLock.unlock();

                //notifyServerStatusChangeListener(changedServers);

            } catch (Exception e) {
                LOG.error("LoadBalancer [{}]: Error pinging", name, e);
            } finally {
                pingInProgress.set(false);
            }
        }
    }

}
