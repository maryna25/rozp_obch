package main

import (
	"fmt"
	"time"
)

func main() {
	channel := make(chan int)
	jug := make(chan []bool)
	go bear(channel)
	for i := 0; i < 10; i++ {
		go bee(channel, jug)
	}

	jug <- make([]bool, 0)

	var input string
	fmt.Scanln(&input)
}

func bear(channel chan int)  {
	for {
		eat := <- channel
		fmt.Println("Bear: I have eaten", eat, "jug of honey!")
		channel <- 0
	}
}

func bee(channel chan int, jug chan []bool)  {
	for {
		res := <-jug
		if len(res) >= 20 {
			fmt.Println("Bee: wake up, bear!")
			channel <- 1
			work := <- channel
			fmt.Println("Bee: There are", work, "swallows of honey")
			jug <- make([]bool, 0)
		} else {
			res = append(res, true)
			jug <- res
		}

		time.Sleep(time.Second)
	}
}
