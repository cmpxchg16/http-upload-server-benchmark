var http = require("http");
var fs = require("fs");

var counter = 0;
var port = process.argv[2];

function postRequest(request, response, callback) {
    if (request.method == 'POST') {
	var writeStream = fs.createWriteStream("/tmp/" + (++counter) + port);

        request.on('data', function (data) {
		writeStream.write(data);
	});

        request.on('end', function () {
	    writeStream.end();
	    callback();
        });
    }
}

http.createServer(function(request, response) {
    postRequest(request, response, function() {
        response.writeHead(200, "OK", {'Content-Type': 'text/plain'});
        response.end();
    });

}).listen(port);
