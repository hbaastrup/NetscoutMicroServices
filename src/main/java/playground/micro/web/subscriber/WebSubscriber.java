package playground.micro.web.subscriber;

import java.util.List;
import java.util.Random;

import hba.gc.GcEvent;
import hba.gc.GcEventListener;
import hba.gc.GcEventLogger;
import io.javalin.BadRequestResponse;
import io.javalin.Javalin;
import playground.micro.models.CommandMetricsHolder;
import playground.micro.models.MonitorMetric;
import playground.micro.models.Subscriber;

public class WebSubscriber implements GcEventListener {
	Javalin app;
	String name;
	SubscriberCacheController controller;
	Random rand = new Random();
	long meanProssingTime = 500; //in milliseconds
	long stdDevProssingTime = 600; //in milliseconds
	boolean simulateSlowResponse = false;
	
	GcEventLogger gcEventLogger;
	
	public WebSubscriber(int port, String name, String tacServiceUrl) {
		gcEventLogger = new GcEventLogger();
		gcEventLogger.start(this);
		this.name = name;
		controller = new SubscriberCacheController(tacServiceUrl);
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);
		
		app.get("/micro/sub/get/:calling", ctx -> {
			Subscriber p = controller.getPhone(ctx.pathParam("calling"));
			if (p==null)
				throw new BadRequestResponse("ERROR: Subscriber does not exist");
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(p);
		});
		
		app.get("/micro/sub/all", ctx -> {
			List<Long> all = controller.getAllPhones();
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(all);
		});
		
		app.get("/micro/get/name", ctx -> {
			ctx.result(name);
		});
		
		app.get("/micro/metic", ctx -> {
			MonitorMetric metric = new MonitorMetric().setName(name);
			List<CommandMetricsHolder> mitricList = CommandMetricsHolder.instanceHystrixCommandMetricsList(controller.getTimeout());
			metric.setCommandMetrics(mitricList);
			metric.setGcEvents(gcEventLogger.getLogs());;
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(metric);
		});
		
		app.post("/micro/sub/time/:calling/:time", ctx -> {
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");

			Subscriber p = controller.getPhone(ctx.pathParam("calling"));
			if (p==null)
				throw new BadRequestResponse("ERROR: Subscriber does not exist");
			
			int time = 0;
			try {
				time = Integer.parseInt(ctx.pathParam("time"));
			} catch (NumberFormatException e) {
				throw new BadRequestResponse("ERROR: Time format error");
			}
			
			p.addTime(time);
			ctx.json(p.getTime());
			
			//Simulate an eventually busy service
			if (simulateSlowResponse) {
				long processingTime = (long)(stdDevProssingTime * rand.nextGaussian() + meanProssingTime);
				Thread.sleep(processingTime);
			}
		});
		
		app.get("/micro/fail", ctx -> ctx.status(401).json("'err':'Unauthorized'"));
		app.get("/micro/exception", ctx -> {throw new BadRequestResponse("ERROR: Provoked by GET");});
	}
	
	public void close() {}
	
	public void setSimulateSlowResponse(boolean simulate) {simulateSlowResponse = simulate;}
	
	
	@Override
	public void onComplete(GcEvent event) {}
	
	
	
	public static void main(String[] args) throws Exception {
		int port = 10082;
		String name = WebSubscriber.class.getName();
		String tacEndpoint = "http://localhost:10080";
		boolean simulateSlowResponse = false;
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-n".equals(args[i])) {
				i++;
				if (i<args.length)
					name = args[i];
			}
			else if ("-t".equals(args[i])) {
				i++;
				if (i<args.length)
					tacEndpoint = args[i];
			}
			else if ("-s".equals(args[i])) {
				simulateSlowResponse = true;
			}
		}

		WebSubscriber web = new WebSubscriber(port, name, tacEndpoint);
		web.setSimulateSlowResponse(simulateSlowResponse);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
	}

}
