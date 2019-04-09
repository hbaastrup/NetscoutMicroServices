package playground.micro.subscriber;

import java.util.List;

import io.javalin.BadRequestResponse;
import io.javalin.Javalin;
import playground.micro.models.Subscriber;

public class WebSubscriber {
	Javalin app;
	SubscriberCacheController controller;
	
	public WebSubscriber(int port, String tacServiceUrl) {
		controller = new SubscriberCacheController(tacServiceUrl);
		
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);
		
		app.get("/micro/sub/get/:calling", ctx -> {
			Subscriber p = controller.getPhone(ctx.pathParam("calling"));
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(p);
		});
		
		app.get("/micro/sub/all", ctx -> {
			List<Long> all = controller.getAllPhones();
			ctx.res.setHeader("Access-Control-Allow-Origin", "*");
			ctx.json(all);
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
			ctx.json(p);
		});
	}
	
	public void close() {}
	
	
	public static void main(String[] args) throws Exception {
		int port = 10081;
		String tacEndpoint = "http://localhost:10080";
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-t".equals(args[i])) {
				i++;
				if (i<args.length)
					tacEndpoint = args[i];
			}
		}

		WebSubscriber web = new WebSubscriber(port, tacEndpoint);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
	}
}
