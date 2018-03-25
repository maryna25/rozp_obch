package main

import (
	"fmt"
	"time"
)

func worker(id int, jobs <-chan []int, results chan<- int, terminateChannel <-chan bool) {
	for j := range jobs {
		time.Sleep(time.Second)
		select {
			case <-terminateChannel:
				return
			default:
				fmt.Println("group", id, "started job", j)

				l := 0
				for i:= range j {
					if j[i] == 1 {
						l = 1
						fmt.Println("We found a bear!!!")
						time.Sleep(time.Second)
					}
				}
				results <- l
				fmt.Println("group", id, "finished job", j)
		}
	}
}

func main() {
	matrix := [][]int{{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},
									  {0,0,0,0,0,0,0,0,0,0},{0,1,0,0,0,0,0,0,0,0},
									  {0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},
									  {0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},
									  {0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0}}
	terminateChannel := make(chan bool)
	jobs := make(chan []int, 100)
	results := make(chan int)

	for i := 1; i <= 3; i++ {
		go worker(i, jobs, results, terminateChannel)
	}

	for j := 0; j < 10; j++ {
		jobs <- matrix[j]
	}
	close(jobs)

	for a := 0; a < 10; a++ {
		result := <-results
		if result == 1 {
			terminateChannel <- true
			break
		}
	}
}
