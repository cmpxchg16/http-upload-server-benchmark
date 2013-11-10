import vibe.appmain;
import vibe.core.log;
import vibe.core.file;
import vibe.http.router;
import vibe.http.server;
import vibe.core.core;

import core.atomic;

import std.conv;
import std.exception;


shared(uint) counter = 0;


void uploadFile(HTTPServerRequest req, HTTPServerResponse res)
{
	atomicOp!"+="(counter, 1);

	auto pf = req.bodyReader;
	auto file = openFile("/tmp/" ~ to!string(counter), FileMode.createTrunc);
	file.write(pf);
	scope(exit) file.close();
	
	res.writeBody("", "text/plain");
}

shared static this()
{
	enableWorkerThreads();
	auto router = new URLRouter;
	router.post("/upload", &uploadFile);

	auto settings = new HTTPServerSettings;
	settings.port = 8080;
	settings.bindAddresses = ["::1", "127.0.0.1"];
	listenHTTP(settings, router);
}
