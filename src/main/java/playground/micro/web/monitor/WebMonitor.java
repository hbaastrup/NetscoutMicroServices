package playground.micro.web.monitor;

import java.util.List;

import io.javalin.Javalin;
import playground.micro.models.EnpointStatus;
import playground.micro.monitor.Monitor;

public class WebMonitor {
	Javalin app;
	String name;
	Monitor monitor;
	
	public WebMonitor(int port, String name, Monitor monitor) {
		this.name = name;
		this.monitor = monitor;
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);

		app.get("/micro/monitor/get", ctx -> {
			List<EnpointStatus> status = monitor.getAllStatus();
			ctx.json(status);
		});
		
		app.get("/micro/get/name", ctx -> {
			ctx.result(name);
		});
		
	}
	
	public void close() {}
}
