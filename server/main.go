package main

import (
	"bytes"
	"compress/gzip"
	"encoding/base64"
	"fmt"
	"github.com/sirupsen/logrus"
	"golang.org/x/text/encoding/charmap"
	"golang.org/x/text/encoding/simplifiedchinese"
	"net/http"
)

func compress(s string) string {
	//使用GBK字符集encode
	gbk, err := simplifiedchinese.GBK.NewEncoder().Bytes([]byte(s))
	if err != nil {
		logrus.Error(err)
		return ""
	}

	//转为ISO8859_1，也就是latin1字符集
	latin1, err := charmap.ISO8859_1.NewDecoder().Bytes(gbk)
	if err != nil {
		return ""
	}

	//使用gzip压缩
	var buf bytes.Buffer
	zw := gzip.NewWriter(&buf)

	_, err = zw.Write(latin1)
	if err != nil {
		logrus.Fatal(err)
	}

	if err := zw.Close(); err != nil {
		logrus.Fatal(err)
	}

	//使用base64编码
	encoded := base64.StdEncoding.EncodeToString(buf.Bytes())
	fmt.Println(encoded)
	return encoded
}

func handler(writer http.ResponseWriter, request *http.Request) {
	fmt.Fprintf(writer, compress("Go程序设计语言"))
}
func main() {
	http.HandleFunc("/", handler)
	http.ListenAndServe(":8080", nil)
}
