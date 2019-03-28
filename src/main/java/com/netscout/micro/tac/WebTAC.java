package com.netscout.micro.tac;

import java.io.File;
import java.util.List;

import io.javalin.Javalin;

public class WebTAC {
	Javalin app;
	TACCache cache = new TACCache();
	
	public WebTAC(int port, TACCache cache) {
		this.cache = cache;
		app = Javalin.create();
		app.enableRouteOverview("/path"); // render a HTML page showing all mapped routes
		app.enableStaticFiles(".");
		app.start(port);
		
		app.get("/micro/tac/get/:tac", ctx -> {
			TAC tac = cache.get(ctx.pathParam("tac"));
			ctx.json(tac);
		});
		
		app.get("/micro/tac/all", ctx -> {
			List<Integer> all = cache.getAllTacs();
			ctx.json(new TACArray(all));
		});
		
		app.get("/micro/fail", ctx -> ctx.status(401).json("'err':'Unauthorized'"));
	}
	
	public void close() {}
	
	
	public static void main(String[] args) throws Exception {
		String dataFile = "data/netscout-avvasi_20190315_84_of_84.txt";
		int port = 10080;
		
		for (int i=0; i<args.length; i++) {
			if ("-p".equals(args[i])) {
				i++;
				if (i<args.length)
					port = Integer.parseInt(args[i]);
			}
			else if ("-d".equals(args[i])) {
				i++;
				if (i<args.length)
					dataFile = args[i];
			}
		}
		
		TACCache cache = new TACCache();
		cache.load(new File(dataFile));
		WebTAC web = new WebTAC(port, cache);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("STOPPING!!!");
				web.close();
				System.out.println("STOPPED!!!");
			}
		});
	}
}
