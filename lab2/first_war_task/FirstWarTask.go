package main

import (
	"fmt"
	"math/rand"
	"time"
)

func main() {
	stolen := make(chan int)
	channel := make(chan int)

	go stealFromStorage(stolen)
	go putInCar(stolen, channel)
	go countPrice(channel)

	var input string
	fmt.Scan(&input)
}

func stealFromStorage(stolen chan int) {
	for {
		stolen <- rand.Intn(100)
	}
}

func putInCar(stolen chan int, c chan int) {
	for {
		val := <-stolen
		c <- val
	}
}

func countPrice(c chan int) {
	for {
		value := <-c
		fmt.Println(value)
		time.Sleep(time.Millisecond * 500)
	}
}
