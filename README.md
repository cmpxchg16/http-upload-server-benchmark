Introduction
================

The goal of that project is to provide few implementations to http upload server in some languages with diffrent programming approaches.
The implementation are very simple: receiving http post requests, and persist the payload to filesystem.
The benchmarks include 2 persistent options:

1. SSD device
2. tmpfs

The Go & cmpxchg16/Mordor & vibe.d programming approach based on green threads (goroutines & Fibers) with synchronous handling.
The Java (Netty & Grizzly frameworks) & Node.js programming approach based on event asynchronous callback handling.


Benchmark
===========

###Go:

####SSD:
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            89383 hits
    Successful requests:                 89383 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            8938 hits/sec
    Read throughput:                   1036842 bytes/sec
    Write throughput:                 71257656 bytes/sec
    Test time:                              10 sec

####tmpfs:
    Dispatching 500 clients
    Waiting for results...

    Requests:                           115248 hits
    Successful requests:                115247 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:           11524 hits/sec
    Read throughput:                   1336876 bytes/sec
    Write throughput:                 91764221 bytes/sec
    Test time:                              10 sec
    
###Node.js:

####SSD:
    $>cat urls.txt
    http://169.254.145.145:8080/upload
    http://169.254.145.145:8181/upload
    http://169.254.145.145:8282/upload
    http://169.254.145.145:8383/upload

    $>go run gobench.go -k=true -f urls.txt -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            68786 hits
    Successful requests:                 68765 hits
    Network failed:                         21 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            6876 hits/sec
    Read throughput:                    701403 bytes/sec
    Write throughput:                 54914084 bytes/sec
    Test time:                              10 sec

####tmpfs:
    Dispatching 500 clients
    Waiting for results...

    Requests:                            81774 hits
    Successful requests:                 81764 hits
    Network failed:                         10 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            8176 hits/sec
    Read throughput:                    834003 bytes/sec
    Write throughput:                 65226827 bytes/sec
    Test time:                              10 sec

###Java:
    
####Grizzly:
####SSD:
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            97637 hits
    Successful requests:                 97637 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            9763 hits/sec
    Read throughput:                    791055 bytes/sec
    Write throughput:                 77802220 bytes/sec
    Test time:                              10 sec

####tmpfs:
    Dispatching 500 clients
    Waiting for results...

    Requests:                           135187 hits
    Successful requests:                135185 hits
    Network failed:                          2 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:           13518 hits/sec
    Read throughput:                   1095460 bytes/sec
    Write throughput:                107571860 bytes/sec
    Test time:                              10 sec

####Netty:
####SSD:
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            96389 hits
    Successful requests:                 96389 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            9638 hits/sec
    Read throughput:                    597611 bytes/sec
    Write throughput:                 76813599 bytes/sec
    Test time:                              10 sec

####tmpfs:
    Dispatching 500 clients
    Waiting for results...

    Requests:                           134425 hits
    Successful requests:                134419 hits
    Network failed:                          6 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:           13441 hits/sec
    Read throughput:                    833397 bytes/sec
    Write throughput:                106967364 bytes/sec
    Test time:                              10 sec

###cmpxchg16/Mordor:
####SSD:
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            97931 hits
    Successful requests:                 97931 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            9793 hits/sec
    Read throughput:                    372137 bytes/sec
    Write throughput:                 78036096 bytes/sec
    Test time:                              10 sec

####tmpfs:
    Dispatching 500 clients
    Waiting for results...

    Requests:                           139121 hits
    Successful requests:                139117 hits
    Network failed:                          4 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:           13911 hits/sec
    Read throughput:                    528648 bytes/sec
    Write throughput:                110690736 bytes/sec
    Test time:                              10 sec

###vibe.d:
####SSD
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            55803 hits
    Successful requests:                 55803 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            5580 hits/sec
    Read throughput:                   1183023 bytes/sec
    Write throughput:                 60244210 bytes/sec
    Test time:                              10 sec

####tmpfs
    $>go run gobench.go -k=true -u http://169.254.145.145:8080/upload -c 500 -t 10 -d gobench.go
    Dispatching 500 clients
    Waiting for results...

    Requests:                            70451 hits
    Successful requests:                 70451 hits
    Network failed:                          0 hits
    Bad requests failed (!2xx):              0 hits
    Successfull requests rate:            7045 hits/sec
    Read throughput:                   1493561 bytes/sec
    Write throughput:                 75917570 bytes/sec
    Test time:                              10 sec


### Conclusions:
1. cmpxchg16/Mordor give the best results both SSD & tmpfs
2. Go better than Node.js & vibe.d
3. Netty & Grizzly looking the ~= same
4. The simplest one is Go


Notes
================

1. The Netty & Grizzly & Node.js implementations are based on examples those frameworks supplied.
2. Because Node.js work in a model of one thread, I run 4 instances of Node.js process and the client round-robin between those processes.
3. The implemetations missing some errors handling, but on each test I verified that the: number of requests == number of successfully files.
4. The implementation of Netty based on full http request handling, because the payload are small it's valid, in case of big payload better implementation are based on streaming chunks to file (look@OrderedMemoryAwareThreadPoolExecutor)
5. The Grizzly thread pool configured with the number of cores (4), Netty provide implementation that based on CachedThreadPool (no limit on number of threads - the system can open how much it need).
6. I ran the test 3 times for each server and choose the best one.
7. The implementations of Netty & Grizzly write to disk in a blocking fashion, a better approach is to implement lazy writer (kind of hell in event model)
8. I don't test on spindle HDD, but if you decide to test it you should also pay attention on the configuration of writeback mechanism in Linux (how the dirty cache buffered pages flushed to disk)
9. To build the cpp example (cmpxchg16/Mordor) - goto project page and see how to build.
10. To build the vibe.d, follow the instructions inside the project documentation (the test done via dub with release build)
11. Environment:
```
    Server:

    Kernel : 3.5.0 - x86_64 (Ubuntu 12.10)
    CPU : Intel(R) Core(TM) i7-3720QM CPU @ 2.60GHz (4 cores)
    RAM : 4 GB 1600 MHz DDR3
    Filesystem: ext4 on a SSD

    Client:

    Kernel : Darwin Kernel Version 11.4.2 x86_64 (Mac OS X Lion 10.7.5)
    CPU : Intel(R) Core(TM) i7-3720QM CPU @ 2.60GHz (4 cores)
    RAM : 8 GB 1600 MHz DDR3
```
12. tcp sysctl server configuration:
```
    net.ipv4.tcp_tw_recycle = 1
    net.ipv4.tcp_tw_reuse = 1
    net.ipv4.tcp_fin_timeout = 1
    net.ipv4.tcp_timestamps = 1
    net.ipv4.tcp_syncookies = 0
    net.ipv4.ip_local_port_range = 1024 65535

    $>sysctl -p /etc/sysctl.conf
```
13. server limits configuration
```
change the maximum of open files:
$>ulimit -n 200000
```
14. The first test was done on SSD, the second one on tmpfs:
```
    mount -t tmpfs -o size=1512m tmpfs /tmp
```


License
================

Licensed under the New BSD License.


Author
================

Uri Shamay (shamayuri@gmail.com)
