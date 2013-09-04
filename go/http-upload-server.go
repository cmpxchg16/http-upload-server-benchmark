package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"runtime"
	"strconv"
	"sync/atomic"
)

var x int64

func upload(w http.ResponseWriter, r *http.Request) {
	out, err := os.Create("/tmp/" + strconv.FormatInt(atomic.AddInt64(&x, 1), 10))

	if err != nil {
		fmt.Println(err)
	}

	io.Copy(out, r.Body)
}

func main() {
	runtime.GOMAXPROCS(runtime.NumCPU())
	http.HandleFunc("/upload", upload)
	http.ListenAndServe(os.Args[1], nil)
}
